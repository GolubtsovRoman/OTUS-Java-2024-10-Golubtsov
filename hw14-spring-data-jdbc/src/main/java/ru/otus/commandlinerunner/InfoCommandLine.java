package ru.otus.commandlinerunner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
@Slf4j
public class InfoCommandLine implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("""
                
                ====================================
                The main page: http://localhost:8080
                ====================================""");
    }

}
