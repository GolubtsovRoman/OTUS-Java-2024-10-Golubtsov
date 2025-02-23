package ru.otus.controller.rest.dto;

import java.util.List;

public record ClientViewDto(String name,
                            String address,
                            List<String> phones) {
}
