package ru.otus.java.dev.pro.crm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.dev.pro.crm.model.dto.BloodBankDto;
import ru.otus.java.dev.pro.crm.model.entity.BloodBank;
import ru.otus.java.dev.pro.crm.repository.BloodBankRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodBankService {

    private final BloodBankRepository bloodBankRepository;
    private final Converter<BloodBank, BloodBankDto> bankBloodBankDtoConverter;

    @Transactional
    public List<BloodBankDto> getAll() {
        return bloodBankRepository.findAll().stream().map(bankBloodBankDtoConverter::convert).toList();
    }

}
