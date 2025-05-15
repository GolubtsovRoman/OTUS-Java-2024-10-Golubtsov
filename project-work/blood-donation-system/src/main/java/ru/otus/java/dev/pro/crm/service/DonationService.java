package ru.otus.java.dev.pro.crm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.dev.pro.crm.exception.EntityNotFoundException;
import ru.otus.java.dev.pro.crm.model.dto.DonationDto;
import ru.otus.java.dev.pro.crm.model.entity.Donation;
import ru.otus.java.dev.pro.crm.repository.DonationRepository;
import ru.otus.java.dev.pro.crm.repository.DonorRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final Converter<Donation, DonationDto> donationConverter;
    private final DonorRepository donorRepository;

    @Transactional
    public DonationDto addDonation(long donorId, int bloodVolumeMl) {
        if (bloodVolumeMl < 1) {
            var errMsg = "Blood volume can't less than ONE. You volume: %d".formatted(bloodVolumeMl);
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        var donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new EntityNotFoundException("Donor not found with id: " + donorId));

        var newDonation = Donation.builder()
                .donor(donor)
                .donationDate(LocalDate.now())
                .bloodVolume(bloodVolumeMl)
                .build();
        var savedDonation = donationRepository.save(newDonation);
        var donationDto = donationConverter.convert(savedDonation);
        log.debug("Registered a new donation: {}", newDonation);
        return donationDto;
    }

    @Transactional
    public Optional<DonationDto> lastDonation(long donorId) {
        return donationRepository.findTopByDonorIdOrderByDonationDateDesc(donorId)
                .map(donationConverter::convert);
    }

    @Transactional
    public List<DonationDto> getByDonorId(long donorId) {
        return donationRepository.findByDonorId(donorId).stream()
                .map(donationConverter::convert)
                .toList();
    }

    @Transactional
    public List<DonationDto> getAll() {
        return donationRepository.findAll().stream()
                .map(donationConverter::convert)
                .toList();
    }

}
