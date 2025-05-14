package ru.otus.java.dev.pro.crm.model.converter;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.otus.java.dev.pro.crm.model.enumz.BloodGroup;

@Converter(autoApply = true)
public class BloodGroupConverter implements AttributeConverter<BloodGroup, String> {

    @Override
    public String convertToDatabaseColumn(BloodGroup bloodGroup) {
        return (bloodGroup != null) ? bloodGroup.getCode() : null;
    }

    @Override
    public BloodGroup convertToEntityAttribute(String code) {
        return (code != null) ? BloodGroup.fromCode(code) : null;
    }
}
