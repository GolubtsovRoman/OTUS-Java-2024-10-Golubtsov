package ru.otus.crm.model.dto;

public record PhoneDto(Long id,
                       String number,
                       Long clientId) {
}
