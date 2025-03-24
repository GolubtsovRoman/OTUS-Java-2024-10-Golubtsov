package ru.otus.client;

import io.grpc.ManagedChannelBuilder;
import ru.otus.client.observer.NumberObserver;
import ru.otus.client.service.NumberPrinter;
import ru.otus.protobuf.IntervalMessage;
import ru.otus.protobuf.RemoteNumberGeneratorServiceGrpc;

import java.util.concurrent.CountDownLatch;

public class ClientApp {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8191;

    private static final int FIRST_VALUE = 1;
    private static final int LAST_VALUE = 30;


    public static void main(String... args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();
        var stub = RemoteNumberGeneratorServiceGrpc.newStub(channel);

        var latch = new CountDownLatch(1);
        var numberObserver = new NumberObserver(latch);

        System.out.println("numbers Client is starting...");
        stub.generateStreamNumber(buildMessage(FIRST_VALUE, LAST_VALUE), numberObserver);
        NumberPrinter.printStream(numberObserver);

        latch.await();
        channel.shutdown();
    }

    private static IntervalMessage buildMessage(int first, int last) {
        return IntervalMessage.newBuilder()
                .setFirstValue(first)
                .setLastValue(last)
                .build();
    }

}
