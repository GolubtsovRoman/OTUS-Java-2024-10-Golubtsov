package ru.otus.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.client.observer.NumberObserver;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class NumberPrinter {

    private static final Logger log = LoggerFactory.getLogger(NumberPrinter.class);

    private static final int CYCLE_END = 50;
    private static final Duration SLEEP_TIME = Duration.of(1, ChronoUnit.SECONDS);

    public static void printStream(NumberObserver numberObserver) {
        Integer rememberValueServer = null;
        var currentValue = 0;
        for (int i = 0; i < CYCLE_END; i++) {
            var serverValue = numberObserver.getLastValue();
            if (!Objects.equals(rememberValueServer, serverValue)) {
                currentValue += numberObserver.getLastValue() + 1;
            } else {
                ++currentValue;
            }
            log.info("currentValue:{}", currentValue);
            rememberValueServer = serverValue;

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ignore) {
                System.out.println(ignore.getMessage());;
            }
        }
    }

}
