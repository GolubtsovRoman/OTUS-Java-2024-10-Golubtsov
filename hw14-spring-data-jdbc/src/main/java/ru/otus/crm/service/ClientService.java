package ru.otus.crm.service;

import ru.otus.controller.rest.dto.ClientViewDto;
import ru.otus.crm.model.dto.ClientDto;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    ClientDto save(ClientViewDto clientViewDto);

    Optional<ClientDto> find(long id);

    List<ClientDto> findAll();

}
