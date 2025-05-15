package ru.otus.java.dev.pro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.java.dev.pro.controller.action.dto.AnaliseDto;
import ru.otus.java.dev.pro.controller.action.dto.ApproveDto;
import ru.otus.java.dev.pro.crm.model.dto.DonationDto;
import ru.otus.java.dev.pro.crm.model.dto.DonorDto;
import ru.otus.java.dev.pro.crm.model.dto.PersonDto;
import ru.otus.java.dev.pro.crm.model.enumz.BloodGroup;
import ru.otus.java.dev.pro.crm.model.enumz.Gender;
import ru.otus.java.dev.pro.crm.service.BlacklistService;
import ru.otus.java.dev.pro.crm.service.DonationService;
import ru.otus.java.dev.pro.crm.service.DonorService;
import ru.otus.java.dev.pro.crm.service.PersonService;
import ru.otus.java.dev.pro.exception.DonationException;
import ru.otus.java.dev.pro.exception.PersonNotFoundException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class DonationManagementService implements Resealable {

    private final PersonService personService;
    private final DonorService donorService;
    private final DonationService donationService;
    private final BlacklistService blacklistService;
    private final BloodPoolService bloodPoolService;

    private final Map<String, PersonDto> persons = new ConcurrentHashMap<>();
    private final Map<String, DonorDto> donorsToAnalise = new ConcurrentHashMap<>();
    private final Map<String, DonorDto> donorsToCheck = new ConcurrentHashMap<>();
    private final Map<String, DonorDto> donorsToDonate = new ConcurrentHashMap<>();

    private static final int MALE_REST_DAYS = 60;
    private static final int FEMALE_REST_DAYS = 90;


    public PersonDto sendToDonation(String snils) {
        var personDto = personService.getBySnils(snils);
        persons.put(snils, personDto);
        return personDto;
    }

    public DonorDto sendToAnalise(String snils) {
        var personDto = persons.get(snils);
        if (personDto == null) {
            var errMsg = "Person with snils=%s not found in donation list".formatted(snils);
            log.error(errMsg);
            throw new DonationException(errMsg);
        }
        var personId = personDto.id();

        var donorDto = donorService.getByPersonId(personId)
                .map(dto -> checkAndReturnExistDonor(dto, personDto.gender()))
                .orElseGet(() -> registerNewDonor(personId));

        donorsToAnalise.put(donorDto.id().toString(), donorDto);
        persons.remove(snils);

        log.debug("Person with ID={} moved to analise", personId);
        return donorDto;
    }

    public DonorDto sendToCheck(long donorId, AnaliseDto analiseDto) {
        var extractedDonorId = extractDonorFromMap(donorId, donorsToAnalise).id();

        if (!analiseDto.isOk()) {
            var errMsg = "Donor with ID=%d has a bad analise".formatted(extractedDonorId);
            log.warn(errMsg);
            throw new DonationException(errMsg);
        }

        var updatedDonorDto =
                donorService.changeBloodGroupIfNeed(extractedDonorId, BloodGroup.fromCode(analiseDto.bloodGroupCode()));

        donorsToCheck.put(String.valueOf(extractedDonorId), updatedDonorDto);
        log.debug("Donor with ID={} moved to checking", extractedDonorId);
        return updatedDonorDto;
    }

    public Optional<DonorDto> sendToDonation(long donorId, ApproveDto approveDto) {
        var donorDto = extractDonorFromMap(donorId, donorsToCheck);
        var extractedDonorId = donorDto.id();

        Optional<DonorDto> optionalDonorTOdonateDto;
        if (approveDto.isApprove()) {
            donorsToDonate.put(String.valueOf(extractedDonorId), donorDto);
            optionalDonorTOdonateDto = Optional.of(donorDto);
        } else {
            if (approveDto.toBlacklist()) {
                var blacklistDto = blacklistService.addToBlacklist(donorId, approveDto.comment());
                log.debug("Created a new record in blacklist: {}", blacklistDto);
            } else {
                log.debug("Donor with ID={} can't donate. Reason: {}", donorId, approveDto.comment());
            }
            optionalDonorTOdonateDto = Optional.empty();
        }

        return optionalDonorTOdonateDto;
    }

    public DonationDto makeDonation(long donorId, int bloodVolumeMl) {
        var extractedDonorId = extractDonorFromMap(donorId, donorsToDonate).id();
        var donationDto = donationService.addDonation(extractedDonorId, bloodVolumeMl);
        bloodPoolService.addBlood(donationDto);
        return donationDto;
    }

    @Override
    public void reset() {
        log.warn("Will clear {} record(s) from person map", persons.size());
        persons.clear();

        log.warn("Will clear {} record(s) from donors to analise map", donorsToAnalise.size());
        donorsToAnalise.clear();

        log.warn("Will clear {} record(s) from donors to check map", donorsToCheck.size());
        donorsToCheck.clear();

        log.warn("Will clear {} record(s) from donors to donate map", donorsToDonate.size());
        donorsToDonate.clear();
    }


    private DonorDto checkAndReturnExistDonor(DonorDto existsDonorDto, Gender donorGender) {
        var existsDonorId = existsDonorDto.id();
        log.debug("Person with ID={} already donor with ID={}", existsDonorDto.personId(), existsDonorId);

        var optionalBlacklistDto = blacklistService.findInBlacklist(existsDonorId);
        if (optionalBlacklistDto.isPresent()) {
            var entryDate = optionalBlacklistDto.get().entryDate();
            var comment = optionalBlacklistDto.get().comment();
            var errMsg = "Donor with ID=%d in blacklist since %s by reason: %s"
                    .formatted(existsDonorId, entryDate, comment);
            log.error(errMsg);
            throw new DonationException(errMsg);
        }

        var optionalLastDonation = donationService.lastDonation(existsDonorId);
        if (optionalLastDonation.isPresent()) {
            var lastDonationDto = optionalLastDonation.get();
            log.debug("Donor with ID={} has donation with ID={}", existsDonorId, lastDonationDto.id());
            var canDonate = switch (donorGender) {
                case MALE ->
                        ChronoUnit.DAYS.between(lastDonationDto.donationDate(), LocalDate.now()) > MALE_REST_DAYS;
                case FEMALE ->
                        ChronoUnit.DAYS.between(lastDonationDto.donationDate(), LocalDate.now()) > FEMALE_REST_DAYS;
            };
            if (!canDonate) {
                var errMsg = "The rest period is not completed for donor with ID=%d (%s). Last donation date: %s"
                        .formatted(existsDonorId, donorGender, lastDonationDto.donationDate());
                log.error(errMsg);
                throw new DonationException(errMsg);
            }
        }

        return existsDonorDto;
    }

    private DonorDto registerNewDonor(long personId) {
        var newDonorDto = donorService.registrationNewDonor(personId);
        log.debug("Person with ID={} not donor. Registered new donor, ID={}", personId, newDonorDto.id());
        return newDonorDto;
    }

    private DonorDto extractDonorFromMap(long donorId, Map<String, DonorDto> donors) {
        var extractedDonorDto = donors.remove( String.valueOf(donorId) );
        if (extractedDonorDto == null) {
            var errMsg = "Donor with ID=%d not found in donation list".formatted(donorId);
            log.error(errMsg);
            throw new DonationException(errMsg);
        }
        return extractedDonorDto;
    }

}
