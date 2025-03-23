package ru.otus.server;

import io.grpc.ServerBuilder;
import ru.otus.server.service.RemoteNumberGeneratorServiceImpl;

import java.io.IOException;

public class ServerApp {

    public static final int SERVER_PORT = 8191;

    public static void main(String ...args) throws IOException, InterruptedException {
        var server = ServerBuilder.forPort(SERVER_PORT)
                .addService(new RemoteNumberGeneratorServiceImpl())
                .build();
        server.start();
        System.out.println("server waiting for client connections...");
        server.awaitTermination();
    }

}
