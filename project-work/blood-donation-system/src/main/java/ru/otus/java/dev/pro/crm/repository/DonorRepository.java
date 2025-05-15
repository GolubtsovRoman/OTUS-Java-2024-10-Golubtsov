package ru.otus.java.dev.pro.crm.repository;

import org.springframework.data.repository.ListCrudRepository;
import ru.otus.java.dev.pro.crm.model.entity.Donor;

import java.util.Optional;

public interface DonorRepository extends ListCrudRepository<Donor, Long> {

    Optional<Donor> findByPersonId(long personId);

}
