package ru.otus.java.dev.pro.crm.model.dto;

import ru.otus.java.dev.pro.crm.model.enumz.Gender;

import java.time.LocalDate;

public record PersonDto(Long id,
                        String fullName,
                        Gender gender,
                        String snils,
                        String phone,
                        String email,
                        LocalDate birthDate,
                        String workplace,
                        String position) {
}
