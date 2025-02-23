package ru.otus.crm.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.crm.model.entity.Client;

public interface ClientRepository extends ListCrudRepository<Client, Long> {
}
