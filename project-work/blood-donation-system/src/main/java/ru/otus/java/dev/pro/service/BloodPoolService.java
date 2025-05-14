package ru.otus.java.dev.pro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.dev.pro.controller.action.dto.BloodReportDto;
import ru.otus.java.dev.pro.crm.model.dto.DonationDto;
import ru.otus.java.dev.pro.crm.model.entity.BloodBank;
import ru.otus.java.dev.pro.crm.model.enumz.BloodGroup;
import ru.otus.java.dev.pro.crm.repository.BloodBankRepository;
import ru.otus.java.dev.pro.crm.service.BlacklistService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BloodPoolService implements Resealable {

    private final BloodCheckService bloodCheckService;
    private final BlacklistService blacklistService;
    private final BloodBankRepository bloodBankRepository;

    private final List<DonationDto> bloodPool = new ArrayList<>();

    @Transactional
    public void addBlood(DonationDto donationDto) {
        bloodPool.add(donationDto);
        log.debug("{} ml of blood added from donation with ID={}", donationDto.bloodVolume(), donationDto.id());
    }

    @Transactional
    public BloodReportDto flush() {
        log.debug("Will process {} donation(s)", bloodPool.size());
        if (bloodPool.isEmpty()) {
            return new BloodReportDto(0, 0, 0, 0);
        }

        int successDonation = 0;
        long successBloodVolume = 0;
        int rejectedDonation = 0;
        long rejectedBloodVolume = 0;

        Map<BloodGroup, BloodBank> bloodBankMap = bloodBankRepository.findAll().stream()
                .collect(Collectors.toMap(BloodBank::getBloodGroup, bloodBank -> bloodBank));

        log.debug("Checking blood...");
        for (DonationDto donationDto: bloodPool) {
            if (bloodCheckService.bloodIsOk(donationDto)) {
                successDonation++;
                successBloodVolume += donationDto.bloodVolume();
                addBloodToBank(bloodBankMap, donationDto.donorDto().bloodGroup(), donationDto.bloodVolume());
                log.debug("Blood from donor with ID={} is OK. Donation ID={}",
                        donationDto.donorDto().id(), donationDto.id());
            } else {
                rejectedDonation++;
                rejectedBloodVolume += donationDto.bloodVolume();
                var blacklistDto = blacklistService.addToBlacklist(
                        donationDto.donorDto().id(),
                        "Donor has a BAD blood test"
                );
                log.warn("Blood from donor with ID={} is BAD. Donation ID={}. Added to blacklist with ID={}",
                        donationDto.donorDto().id(), donationDto.id(), blacklistDto.id());
            }
        }
        reset();
        bloodBankRepository.saveAll(bloodBankMap.values());

        return BloodReportDto.builder()
                .countOfSuccessDonation(successDonation)
                .successBloodMl(successBloodVolume)
                .countOfRejectedDonation(rejectedDonation)
                .rejectedBloodMl(rejectedBloodVolume)
                .build();
    }

    private void addBloodToBank(Map<BloodGroup, BloodBank> bloodBankMap, BloodGroup bloodGroup, int volume) {
        var bloodBankCell = bloodBankMap.get(bloodGroup);
        Long bankCellBloodVolume = bloodBankCell.getBloodVolume();
        bankCellBloodVolume = bankCellBloodVolume + volume;
        bloodBankCell.setBloodVolume(bankCellBloodVolume);
    }

    @Override
    public void reset() {
        log.warn("Will clear {} record(s) from blood pool", bloodPool.size());
        bloodPool.clear();
    }

}
