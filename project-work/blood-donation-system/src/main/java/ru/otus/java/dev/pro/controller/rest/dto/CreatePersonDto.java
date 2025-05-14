package ru.otus.java.dev.pro.controller.rest.dto;

import ru.otus.java.dev.pro.crm.model.entity.Person;
import ru.otus.java.dev.pro.crm.model.enumz.Gender;

import java.time.LocalDate;

public record CreatePersonDto(
        String fullName,
        Gender gender,
        String snils,
        String phone,
        String email,
        LocalDate birthDate,
        String workplace,
        String position
) {

    public Person toEntity() {
        return Person.builder()
                .fullName(fullName)
                .gender(gender)
                .snils(snils)
                .phone(phone)
                .email(email)
                .birthDate(birthDate)
                .workplace(workplace)
                .position(position)
                .build();
    }

}
