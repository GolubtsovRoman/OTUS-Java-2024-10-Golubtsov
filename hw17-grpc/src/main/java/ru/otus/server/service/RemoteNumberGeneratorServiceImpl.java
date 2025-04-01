package ru.otus.server.service;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.IntervalMessage;
import ru.otus.protobuf.NumberResult;
import ru.otus.protobuf.RemoteNumberGeneratorServiceGrpc;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class RemoteNumberGeneratorServiceImpl
        extends RemoteNumberGeneratorServiceGrpc.RemoteNumberGeneratorServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(RemoteNumberGeneratorServiceImpl.class);

    private static final Duration SLEEP_TIME = Duration.of(2, ChronoUnit.SECONDS);

    @Override
    public void generateStreamNumber(IntervalMessage request, StreamObserver<NumberResult> responseObserver) {
        int begin = request.getFirstValue();
        int end = request.getLastValue();
        if (begin < end) {
            do {
                int newValue = ++begin;
                log.info("new value:{}", newValue);
                responseObserver.onNext(NumberResult.newBuilder().setResultValue(newValue).build());
                sleep();
            } while (begin < end);
            log.info("done");
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new IllegalArgumentException("FIRST >= LAST"));
        }
    }


    private void sleep() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException ignore) {
            System.out.println(ignore.getMessage());
        }
    }

}
