package ru.otus.commandlinerunner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.otus.controller.rest.dto.ClientViewDto;
import ru.otus.crm.service.ClientService;

import java.util.List;

@Order(0)
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "application.database.init-data", havingValue = "true")
public class FillDataCommandLine implements CommandLineRunner {

    private final ClientService clientService;


    @Override
    public void run(String... args) {
        clientService.save(new ClientViewDto(
                "Anna",
                "Nevsky Prospect",
                List.of("123-456-7890", "098-765-4321")
        ));
        clientService.save(new ClientViewDto(
                "Alyona",
                "Rubinstein Street",
                List.of("234-567-8901", "876-543-2109")
        ));
        log.info("The database was filled with data");
    }

}
