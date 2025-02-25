package ru.otus.commandlinerunner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InfoRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("""
                
                ====================================
                The main page: http://localhost:8080
                ====================================""");
    }

}
