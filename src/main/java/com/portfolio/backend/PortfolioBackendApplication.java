package com.portfolio.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PortfolioBackendApplication {

    public static void main(String[] args) {

        // Try to load .env file only if it exists (ignore if missing)
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMissing() // 👈 prevents crash when .env isn't found
                .load();

        // Set system properties if .env is present
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
            System.out.println("Loaded: " + entry.getKey() + " = " + entry.getValue());
        });

        SpringApplication.run(PortfolioBackendApplication.class, args);
    }
}
