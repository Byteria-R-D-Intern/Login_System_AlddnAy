package com.example.login_system.application.service;

  public interface PasswordEncoder {
        boolean matches(String rawPassword, String hashedPassword);
        String encode(String rawPassword);
    }

