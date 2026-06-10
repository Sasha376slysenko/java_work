package org.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApp {
    public static void runServer(String[] args) {
        SpringApplication.run(ServerApp.class, args);
    }
}
