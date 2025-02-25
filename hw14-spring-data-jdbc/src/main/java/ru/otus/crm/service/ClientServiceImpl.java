package ru.otus.crm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.controller.rest.dto.ClientViewDto;
import ru.otus.crm.model.dto.ClientDto;
import ru.otus.crm.model.entity.Address;
import ru.otus.crm.model.entity.Client;
import ru.otus.crm.model.entity.Phone;
import ru.otus.crm.repository.ClientRepository;
import ru.otus.crm.repository.PhoneRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PhoneRepository phoneRepository;
    private final Converter<Client, ClientDto> clientConverter;


    @Override
    @Transactional
    public ClientDto save(ClientViewDto clientViewDto) {
        var savedClient = clientRepository.save(
                new Client(null, clientViewDto.name(), new Address(null, clientViewDto.address()))
        );

        var phones = clientViewDto.phones()
                .stream()
                .map(number -> new Phone(null, number, savedClient.getId()))
                .toList();
        var savedPhones = phoneRepository.saveAll(phones);
        savedClient.setPhones(new HashSet<>(savedPhones));

        return clientConverter.convert(savedClient);
    }

    @Override
    public Optional<ClientDto> find(long id) {
        return clientRepository.findById(id)
                .map(clientConverter::convert);
    }

    @Override
    public List<ClientDto> findAll() {
        return clientRepository.findAll()
                .stream()
                .map(clientConverter::convert)
                .toList();
    }

}
