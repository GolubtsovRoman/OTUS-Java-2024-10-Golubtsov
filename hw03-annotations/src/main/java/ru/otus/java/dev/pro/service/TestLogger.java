package ru.otus.java.dev.pro.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.dev.pro.enums.StatusTest;

import java.util.Map;

import static ru.otus.java.dev.pro.enums.StatusTest.ERROR;
import static ru.otus.java.dev.pro.enums.StatusTest.OK;

public class TestLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunnerService.class);

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void printHeadline(String message) {
        String border = "=".repeat(20);
        LOGGER.info(border);
        LOGGER.info(message);
        LOGGER.info(border);
    }

    public static void printStatistics(Map<String, StatusTest> testingResult) {
        long testCount = testingResult.values().size();
        long countIsOk = testingResult.values().stream().filter(OK::equals).count();
        long countIsError = testingResult.values().stream().filter(ERROR::equals).count(); // можно было testCount - countIsOk, но мы честные

        String statistic = """
                Testing statistic:
                Total: %d
                Ok: %d
                Down: %d
                """.formatted(testCount, countIsOk, countIsError);

        StringBuilder stringBuilder = new StringBuilder();
        testingResult.forEach((methodName, status) ->
                stringBuilder.append('\n').append(methodName).append(" ::: ").append(status));

        printHeadline(statistic + stringBuilder);
    }

}
