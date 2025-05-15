package ru.otus.java.dev.pro.commandlinerunner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InfoRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("""
                Check URSs
                actuator: http://localhost:8080/bds/api/actuator/health
                swagger: http://localhost:8080/bds/api/swagger-ui/index.html
                """);
    }

}
