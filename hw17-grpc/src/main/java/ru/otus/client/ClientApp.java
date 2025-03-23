package ru.otus.client;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import ru.otus.protobuf.IntervalMessage;
import ru.otus.protobuf.NumberResult;
import ru.otus.protobuf.RemoteNumberGeneratorServiceGrpc;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class ClientApp {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8191;

    private static final int FIRST_VALUE = 1;
    private static final int LAST_VALUE = 30;

    private static final int CYCLE_END = 50;

    private static final Duration SLEEP_TIME = Duration.of(1, ChronoUnit.SECONDS);


    public static void main(String... args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();
        var stub = RemoteNumberGeneratorServiceGrpc.newStub(channel);

        System.out.println("numbers Client is starting...");

        var latch = new CountDownLatch(1);
        var numberObserver = new NumberObserver(latch);
        var intervalMessage = IntervalMessage.newBuilder()
                .setFirstValue(FIRST_VALUE)
                .setLastValue(LAST_VALUE)
                .build();
        stub.generateStreamNumber(intervalMessage, numberObserver);

        Integer rememberValueServer = null;
        var currentValue = 0;
        for (int i = 0; i < CYCLE_END; i++) {
            var serverValue = numberObserver.getLastValue();
            if (!Objects.equals(rememberValueServer, serverValue)) {
                currentValue += numberObserver.getLastValue() + 1;
            } else {
                ++currentValue;
            }
            System.out.println("currentValue:" + currentValue);
            rememberValueServer = serverValue;
            Thread.sleep(SLEEP_TIME);
        }

        latch.await();
        channel.shutdown();
    }



    static class NumberObserver implements StreamObserver<NumberResult> {

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

}
