package ru.otus.java.dev.pro.crm.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.otus.java.dev.pro.crm.model.dto.DonorDto;
import ru.otus.java.dev.pro.crm.model.entity.Donor;

@Component
@RequiredArgsConstructor
public class DonorConverter implements Converter<Donor, DonorDto> {

    @Override
    public DonorDto convert(Donor donor) {
        return new DonorDto(
                donor.getId(),
                donor.getPerson().getId(),
                donor.getBloodGroup()
        );
    }

}
