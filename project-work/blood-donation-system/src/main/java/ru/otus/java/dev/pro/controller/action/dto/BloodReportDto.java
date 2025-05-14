package ru.otus.java.dev.pro.controller.action.dto;

import lombok.Builder;

@Builder
public record BloodReportDto(
        int countOfSuccessDonation,
        long successBloodMl,
        int countOfRejectedDonation,
        long rejectedBloodMl
) {
}
