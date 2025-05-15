package ru.otus.java.dev.pro.crm.model.enumz;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public enum BloodGroup {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    private final String code;

    BloodGroup(String code) {
        this.code = code;
    }

    public static BloodGroup fromCode(String code) {
        return Arrays.stream(values())
                .filter(bloodGroup -> Objects.equals(bloodGroup.getCode(), code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown blood group code: " + code));
    }

}
