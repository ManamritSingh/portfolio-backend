package com.portfolio.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // Vite runs on 5173, not 3000!
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Portfolio Backend is running!";
    }

    @GetMapping("/api/test")
    public String test() {
        return "Backend API is working!";
    }
}
