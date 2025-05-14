package ru.otus.java.dev.pro.controller.action.dto;

public record ApproveDto(
        boolean isApprove,
        boolean toBlacklist,
        String comment
) {
}
