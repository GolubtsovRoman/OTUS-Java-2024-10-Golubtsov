package ru.otus.server;

import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.server.service.RemoteNumberGeneratorServiceImpl;

import java.io.IOException;

public class ServerApp {

    private static final Logger log = LoggerFactory.getLogger(ServerApp.class);

    public static final int SERVER_PORT = 8191;

    public static void main(String ...args) throws IOException, InterruptedException {
        var server = ServerBuilder.forPort(SERVER_PORT)
                .addService(new RemoteNumberGeneratorServiceImpl())
                .build();
        server.start();
        log.info("server waiting for client connections...");
        server.awaitTermination();
    }

}
