package ru.otus.java.dev.pro.crm.model.dto;

import ru.otus.java.dev.pro.crm.model.enumz.BloodGroup;

public record BloodBankDto(
        BloodGroup bloodGroup,
        Long bloodVolume
) {
}
