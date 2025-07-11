package com.example.login_system.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/test")
    public String test() {
        return "Test endpoint works!";
    }
    
    @GetMapping("/")
    public String home() {
        return "Application is running!";
    }
} 