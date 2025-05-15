package ru.otus.java.dev.pro.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.java.dev.pro.crm.model.entity.Blacklist;
import ru.otus.java.dev.pro.crm.model.entity.Donor;

import java.util.Optional;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {

    Optional<Blacklist> findBlacklistByDonor(Donor donor);

}
