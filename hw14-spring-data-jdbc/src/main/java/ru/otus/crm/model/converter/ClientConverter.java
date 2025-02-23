package ru.otus.crm.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.otus.crm.model.dto.ClientDto;
import ru.otus.crm.model.dto.PhoneDto;
import ru.otus.crm.model.entity.Client;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ClientConverter implements Converter<Client, ClientDto> {

    private final AddessConverter addessConverter;
    private final PhoneConverter phoneConverter;


    @Override
    public ClientDto convert(Client source) {
        var addressDto = source.getAddress() != null ?
                addessConverter.convert(source.getAddress())
                : null;
        List<PhoneDto> phonesDtoList = source.getPhones() != null ?
                source.getPhones().stream().map(phoneConverter::convert).toList()
                : Collections.emptyList();

        return new ClientDto(
                source.getId(),
                source.getName(),
                addressDto,
                phonesDtoList
        );
    }

}
