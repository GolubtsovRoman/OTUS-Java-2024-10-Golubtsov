package ru.otus.crm.model.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.otus.crm.model.dto.AddressDto;
import ru.otus.crm.model.entity.Address;

@Component
public class AddessConverter implements Converter<Address, AddressDto> {

    @Override
    public AddressDto convert(Address source) {
        return new AddressDto(source.id(), source.street());
    }

}
