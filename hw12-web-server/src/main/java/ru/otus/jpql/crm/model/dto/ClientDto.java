package ru.otus.jpql.crm.model.dto;

import ru.otus.jpql.crm.model.entity.Address;
import ru.otus.jpql.crm.model.entity.Client;
import ru.otus.jpql.crm.model.entity.Phone;

import java.util.List;

public record ClientDto(String name,
                        String address,
                        List<String> phones) {

    public Client toEntity() {
        return new Client(
                null,
                name,
                new Address(null, address),
                phones.stream().map(phone -> new Phone(null, phone)).toList()
        );
    }

}
