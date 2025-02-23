package ru.otus.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.controller.rest.dto.ClientViewDto;
import ru.otus.crm.model.dto.ClientDto;
import ru.otus.crm.service.ClientService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClientRestController {

    private final ClientService clientService;


    @PostMapping("/api/client")
    public ClientDto create(@RequestBody ClientViewDto clientViewDto) {
        return clientService.save(clientViewDto);
    }

    @GetMapping("/api/client/{id}")
    public ClientDto read(@PathVariable long id) {
        return clientService.find(id).orElse(null);
    }

    @GetMapping("/api/client")
    public List<ClientDto> readAll() {
        return clientService.findAll();
    }

}
