package com.portfolio.backend.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "helloworld";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("Hashed Password: " + encodedPassword);
    }
}
// $2a$10$do4YWc98/rVCC/PeYYNRc.RT381ByDLf7WYRZ1DL9JVPuWmp992KO