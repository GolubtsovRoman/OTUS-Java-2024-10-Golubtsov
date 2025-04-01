package ru.otus.client.observer;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.NumberResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class NumberObserver implements StreamObserver<NumberResult> {

    private static final Logger log = LoggerFactory.getLogger(NumberObserver.class);

    private final CountDownLatch latch;
    private final AtomicReference<Integer> lastValue = new AtomicReference<>(0);

    public NumberObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(NumberResult numberResult) {
        log.info("new value:{}", numberResult.getResultValue());
        this.lastValue.set(numberResult.getResultValue());
    }

    @Override
    public void onError(Throwable throwable) {
        log.info("ERROR::{}", throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        log.info("request completed");
        this.latch.countDown();
    }

    public int getLastValue() {
        return this.lastValue.getAndSet(0);
    }

}
