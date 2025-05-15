package ru.otus.java.dev.pro.crm.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.java.dev.pro.crm.model.converter.BloodGroupConverter;
import ru.otus.java.dev.pro.crm.model.enumz.BloodGroup;

@Getter
@Setter
@Entity
@Table(name = "blood_bank")
@NoArgsConstructor
@AllArgsConstructor
public class BloodBank {

    @Id
    private Long id;

    @Convert(converter = BloodGroupConverter.class)
    @Column(name = "blood_group", unique = true, nullable = false)
    private BloodGroup bloodGroup;

    @Column(name = "blood_volume", nullable = false)
    private Long bloodVolume = 0L;

}
