package ru.otus.java.dev.pro.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.java.dev.pro.crm.model.entity.BloodBank;

public interface BloodBankRepository extends JpaRepository<BloodBank, Long> {
}
