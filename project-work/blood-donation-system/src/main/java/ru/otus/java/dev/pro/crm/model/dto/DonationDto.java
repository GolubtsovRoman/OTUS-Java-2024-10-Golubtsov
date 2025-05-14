package ru.otus.java.dev.pro.crm.model.dto;

import java.time.LocalDate;

public record DonationDto(Long id,
                          DonorDto donorDto,
                          LocalDate donationDate,
                          Integer bloodVolume) {
}
