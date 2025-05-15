package ru.otus.java.dev.pro.crm.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.otus.java.dev.pro.crm.model.dto.BloodBankDto;
import ru.otus.java.dev.pro.crm.model.dto.DonorDto;
import ru.otus.java.dev.pro.crm.model.entity.BloodBank;
import ru.otus.java.dev.pro.crm.model.entity.Donor;

@Component
@RequiredArgsConstructor
public class BloodBankConverter implements Converter<BloodBank, BloodBankDto> {

    @Override
    public BloodBankDto convert(BloodBank bloodBank) {
        return new BloodBankDto(bloodBank.getBloodGroup(), bloodBank.getBloodVolume());
    }

}
