package ru.otus.java.dev.pro;

import ru.otus.java.dev.pro.service.TestRunnerService;

public class Main {

    public static void main(String[] args) {
        new TestRunnerService().run("ru.otus.java.dev.pro.service.TestService");
    }

}
