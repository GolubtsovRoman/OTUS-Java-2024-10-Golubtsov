package ru.otus.java.dev.pro;

import java.util.Objects;
import java.util.UUID;

public record Banknote(Denomination denomination, String serialNumber) {

    public Banknote(Denomination denomination) {
        this(denomination, UUID.randomUUID().toString());
        if (denomination == null) {
            throw new IllegalArgumentException();
        }
    }

    public Integer getValue() {
        return denomination.getNum();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Banknote banknote = (Banknote) object;
        return Objects.equals(serialNumber, banknote.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(serialNumber);
    }

}
