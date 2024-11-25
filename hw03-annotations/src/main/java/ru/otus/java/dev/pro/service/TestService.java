package ru.otus.java.dev.pro.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.dev.pro.annotation.After;
import ru.otus.java.dev.pro.annotation.Before;
import ru.otus.java.dev.pro.annotation.Test;

@SuppressWarnings("unused")
public class TestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestService.class);

    @Before
    void beforeOne() {
        LOGGER.info("Some logic before the test 1");
    }

    @Before
    void beforeTwo() {
        LOGGER.info("Some logic before the test 2");
    }

    @Test
    void goodTestOne() {
        LOGGER.info("Some test 1");
    }

    @Test
    void badTest() {
        LOGGER.info("Broken test 2");
        throw new RuntimeException("poop code");
    }

    @Test
    void goodTestTwo() {
        LOGGER.info("Some test 3");
    }

    @After
    void afterOne() {
        LOGGER.info("Some logic after the test 1");
    }

    @After
    void afterTwo() {
        LOGGER.info("Some logic after the test 2");
    }

}
