package ru.otus.java.dev.pro.crm.model.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.otus.java.dev.pro.crm.model.dto.DonationDto;
import ru.otus.java.dev.pro.crm.model.entity.Donation;

@Component
@RequiredArgsConstructor
public class DonationConverter implements Converter<Donation, DonationDto> {

    private final DonorConverter donorConverter;

    @Override
    public DonationDto convert(Donation donation) {
        var donorDto = donorConverter.convert(donation.getDonor());
        return new DonationDto(
                donation.getId(),
                donorDto,
                donation.getDonationDate(),
                donation.getBloodVolume()
        );
    }

}
