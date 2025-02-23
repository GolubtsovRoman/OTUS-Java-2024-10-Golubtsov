package ru.otus.crm.model.dto;

import java.util.List;

public record ClientDto(Long id,
                        String name,
                        AddressDto addressDto,
                        List<PhoneDto> phoneDtoList) {
}
