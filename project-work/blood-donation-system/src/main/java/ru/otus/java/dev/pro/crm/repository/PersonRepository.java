package ru.otus.java.dev.pro.crm.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.java.dev.pro.crm.model.entity.Person;

import java.util.Optional;

public interface PersonRepository extends ListCrudRepository<Person, Long> {

    boolean existsBySnils(String snils);

    Optional<Person> findBySnils(String snils);

}
