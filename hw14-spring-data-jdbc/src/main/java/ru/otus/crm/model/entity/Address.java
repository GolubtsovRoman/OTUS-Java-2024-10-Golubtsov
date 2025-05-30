package ru.otus.crm.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("address")
public record Address(@Id Long id,
                      String street) {
}
