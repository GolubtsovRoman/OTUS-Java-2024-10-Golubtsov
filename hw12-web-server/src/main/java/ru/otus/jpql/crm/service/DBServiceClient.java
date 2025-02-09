package ru.otus.jpql.crm.service;

import ru.otus.jpql.crm.model.entity.Client;

import java.util.List;
import java.util.Optional;

public interface DBServiceClient {

    Client saveClient(Client client);

    Optional<Client> getClient(long id);

    List<Client> findAll();
}
