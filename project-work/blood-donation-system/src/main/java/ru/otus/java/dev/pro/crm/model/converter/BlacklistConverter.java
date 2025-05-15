package ru.otus.java.dev.pro.crm.model.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.otus.java.dev.pro.crm.model.dto.BlacklistDto;
import ru.otus.java.dev.pro.crm.model.entity.Blacklist;

@Component
public class BlacklistConverter implements Converter<Blacklist, BlacklistDto> {

    @Override
    public BlacklistDto convert(Blacklist blacklist) {
        return new BlacklistDto(
                blacklist.getId(),
                blacklist.getDonor().getId(),
                blacklist.getEntryDate(),
                blacklist.getComment()
        );
    }

}
