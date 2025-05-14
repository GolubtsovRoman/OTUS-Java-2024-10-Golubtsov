package ru.otus.java.dev.pro.crm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.dev.pro.crm.exception.EntityAlreadyExistsException;
import ru.otus.java.dev.pro.crm.exception.EntityNotFoundException;
import ru.otus.java.dev.pro.crm.model.dto.PersonDto;
import ru.otus.java.dev.pro.crm.model.entity.Person;
import ru.otus.java.dev.pro.crm.repository.PersonRepository;
import ru.otus.java.dev.pro.controller.rest.dto.CreatePersonDto;
import ru.otus.java.dev.pro.util.Validator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final Converter<Person, PersonDto> personConverter;

    private static final int RED_LINE_AGE = 18;


    @Transactional
    public PersonDto create(CreatePersonDto createPersonDto) {
        validateParameters(createPersonDto);

        Person savedPerson = personRepository.save( createPersonDto.toEntity() );
        return personConverter.convert(savedPerson);
    }

    @Transactional
    public PersonDto getBySnils(String snils) {
        var person = personRepository.findBySnils(snils)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with snils: " + snils));
        return personConverter.convert(person);
    }

    @Transactional
    public List<PersonDto> getAll() {
        return personRepository.findAll().stream()
                .map(personConverter::convert)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        personRepository.deleteById(id);
    }


    private void validateParameters(CreatePersonDto createPersonDto) {
        var snils = createPersonDto.snils();
        Validator.validateSnils(snils);
        if (personRepository.existsBySnils(snils)) {
            var errMsg = "Person with snils=%s already exists".formatted(snils);
            log.error(errMsg);
            throw new EntityAlreadyExistsException(errMsg);
        }

        var birthDate = createPersonDto.birthDate();
        if (ChronoUnit.YEARS.between(birthDate, LocalDate.now()) < RED_LINE_AGE) {
            var errMsg = "Age under %d years".formatted(RED_LINE_AGE);
            log.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        var email = createPersonDto.email();
        Validator.validateEmail(email);
    }

}
