package ru.otus.java.dev.pro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.dev.pro.service.TestRunnerService;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        var testResult = new TestRunnerService().run("ru.otus.java.dev.pro.service.TestService");

        LOGGER.info("{} Result of testing {}", "#".repeat(7), "#".repeat(7));
        LOGGER.info(testResult.printStatistics());
        LOGGER.info("#".repeat(33));
    }

}
