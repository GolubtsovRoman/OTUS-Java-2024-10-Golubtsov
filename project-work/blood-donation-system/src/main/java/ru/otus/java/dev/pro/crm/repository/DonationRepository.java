package ru.otus.java.dev.pro.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.java.dev.pro.crm.model.entity.Donation;

import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByDonorId(Long donorId);

    Optional<Donation> findTopByDonorIdOrderByDonationDateDesc(Long donorId);

}
