package ru.otus.java.dev.pro.model;

import ru.otus.java.dev.pro.enums.StatusTest;

import java.util.Map;

import static ru.otus.java.dev.pro.enums.StatusTest.ERROR;
import static ru.otus.java.dev.pro.enums.StatusTest.OK;

public class TestResult {

    private Map<String, StatusTest> testingResult;

    private long countAll;
    private long countIsOk;
    private long countIsErr;



    public TestResult(Map<String, StatusTest> testingResult) {
        var copyTestingResult = Map.copyOf(testingResult);

        this.testingResult = copyTestingResult;
        this.countAll = copyTestingResult.values().size();
        this.countIsOk = copyTestingResult.values().stream().filter(OK::equals).count();
        this.countIsErr = copyTestingResult.values().stream().filter(ERROR::equals).count(); // можно было testCount - countIsOk, но мы честные
    }

    public Map<String, StatusTest> getTestingResult() {
        return Map.copyOf(this.testingResult);
    }

    public long getCountAll() {
        return this.countAll;
    }

    public long getCountIsOk() {
        return this.countIsOk;
    }

    public long getCountIsErr() {
        return this.countIsErr;
    }

    public String printStatistics() {

        String statistic = """
                Testing statistic:
                Total: %d
                Ok: %d
                Down: %d
                """.formatted(getCountAll(), getCountIsOk(), getCountIsErr());

        StringBuilder stringBuilder = new StringBuilder();
        getTestingResult().forEach((methodName, status) ->
                stringBuilder.append('\n').append(methodName).append(" ::: ").append(status));

        return statistic + stringBuilder;
    }

}
