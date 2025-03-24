package ru.otus.client.observer;

import io.grpc.stub.StreamObserver;
import ru.otus.protobuf.NumberResult;

import java.util.concurrent.CountDownLatch;

public class NumberObserver implements StreamObserver<NumberResult> {

    private final CountDownLatch latch;
    private Integer lastValue;


    public NumberObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(NumberResult numberResult) {
        System.out.println("new value:" + numberResult.getResultValue());
        lastValue = numberResult.getResultValue();
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println("ERROR::" + throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        System.out.println("request completed");
        latch.countDown();
    }

    public Integer getLastValue() {
        return lastValue;
    }

}
