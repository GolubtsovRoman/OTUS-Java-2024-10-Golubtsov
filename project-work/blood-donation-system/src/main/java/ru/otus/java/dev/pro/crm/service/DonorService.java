package ru.otus.java.dev.pro.crm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.dev.pro.crm.exception.EntityNotFoundException;
import ru.otus.java.dev.pro.crm.model.dto.DonorDto;
import ru.otus.java.dev.pro.crm.model.entity.Donor;
import ru.otus.java.dev.pro.crm.model.enumz.BloodGroup;
import ru.otus.java.dev.pro.crm.repository.DonorRepository;
import ru.otus.java.dev.pro.crm.repository.PersonRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DonorService {

    private final DonorRepository donorRepository;
    private final PersonRepository personRepository;
    private final Converter<Donor, DonorDto> donorConverter;

    @Transactional
    public DonorDto getById(long id) {
        return donorRepository.findById(id)
                .map(donorConverter::convert)
                .orElse(null);
    }

    @Transactional
    public List<DonorDto> getAll() {
        return donorRepository.findAll().stream()
                .map(donorConverter::convert)
                .toList();
    }

    @Transactional
    public DonorDto registrationNewDonor(long personId) {
        var person = personRepository.findById(personId)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + personId));

        var newDonor = new Donor();
        newDonor.setPerson(person);
        var savedDonor = donorRepository.save(newDonor);
        return donorConverter.convert(savedDonor);
    }

    @Transactional
    public Optional<DonorDto> getByPersonId(long personId) {
        return donorRepository.findByPersonId(personId)
                .map(donorConverter::convert);
    }

    @Transactional
    public DonorDto changeBloodGroupIfNeed(long donorId, BloodGroup newBloodGroup) {
        var donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new EntityNotFoundException("Donor not found with id: " + donorId));

        if (Objects.nonNull(donor.getBloodGroup()) && Objects.equals(newBloodGroup, donor.getBloodGroup())) {
            log.debug("The blood group did not change in donor with ID={}", donorId);
            return donorConverter.convert(donor);
        } else {
            String prevBloodCode = Objects.isNull(donor.getBloodGroup())
                    ? "null"
                    : donor.getBloodGroup().getCode();
            log.debug("The blood group changed in donor with ID={}. Prev: {}, New: {}",
                    donorId, prevBloodCode, newBloodGroup.getCode());
            donor.setBloodGroup(newBloodGroup);
            var savedDonor = donorRepository.save(donor);
            return donorConverter.convert(savedDonor);
        }
    }

}
