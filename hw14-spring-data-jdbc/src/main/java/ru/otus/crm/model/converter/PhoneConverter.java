package ru.otus.crm.model.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.otus.crm.model.dto.PhoneDto;
import ru.otus.crm.model.entity.Phone;

@Component
public class PhoneConverter implements Converter<Phone, PhoneDto> {

    @Override
    public PhoneDto convert(Phone source) {
        return new PhoneDto(source.id(), source.number(), source.clientId());
    }

}
