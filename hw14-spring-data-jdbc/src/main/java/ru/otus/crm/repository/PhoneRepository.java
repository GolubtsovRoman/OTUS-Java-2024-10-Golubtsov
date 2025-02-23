package ru.otus.crm.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.crm.model.entity.Phone;

public interface PhoneRepository extends ListCrudRepository<Phone, Long> {
}
