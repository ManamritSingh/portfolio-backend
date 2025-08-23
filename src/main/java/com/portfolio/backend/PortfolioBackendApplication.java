package com.portfolio.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class PortfolioBackendApplication {

    public static void main(String[] args) {

        // Load .env file
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .load();

        // Set system properties
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
            System.out.println("Loaded: " + entry.getKey() + " = " + entry.getValue()); // Debug line
        });
        SpringApplication.run(PortfolioBackendApplication.class, args);
    }

}
