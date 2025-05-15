package ru.otus.java.dev.pro.crm.model.dto;

import java.time.LocalDate;

public record BlacklistDto(Long id,
                           Long donorId,
                           LocalDate entryDate,
                           String comment) {
}
