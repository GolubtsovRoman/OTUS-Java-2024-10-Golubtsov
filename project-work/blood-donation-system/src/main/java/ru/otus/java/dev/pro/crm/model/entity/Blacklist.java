package ru.otus.java.dev.pro.crm.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "blacklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "comment", nullable = false, length = 1024)
    private String comment;

    @Override
    public String toString() {
        return "Blacklist{" +
                "id=" + id +
                ", donor=" + donor +
                ", entryDate=" + entryDate +
                ", comment='" + comment + '\'' +
                '}';
    }
}
