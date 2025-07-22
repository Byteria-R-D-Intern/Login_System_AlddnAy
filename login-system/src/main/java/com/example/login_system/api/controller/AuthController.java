package com.example.login_system.api.controller;

import com.example.login_system.api.dto.RegisterRequest;
import com.example.login_system.api.dto.RegisterResponse;
import com.example.login_system.api.dto.LoginRequest;
import com.example.login_system.api.dto.LoginResponse;
import com.example.login_system.application.usecase.UserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserUseCase userUseCase;

    public AuthController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @Operation(summary = "Kayıt olmayı sağlar", description = "Veritabnaına veri ekler ve id ile role u tokenın içine gömer.")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank() ||
            request.getRole() == null || request.getRole().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RegisterResponse(null, "Email, password ve role boş olamaz"));
        }
        return userUseCase.register(request.getEmail(), request.getPassword(), request.getRole())
                .map(token -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(new RegisterResponse(token, "Registration successful")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new RegisterResponse(null, "Email already in use or role invalid")));
    }

    @Operation(summary = "Giriş yapmayı sağlar", description = "Veritabanından doğrulama yapıp bir token döner")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponse(null, "Email and password cannot be empty"));
        }
        return userUseCase.login(request.getEmail(), request.getPassword())
                .map(token -> ResponseEntity.ok(new LoginResponse(token, "Login successful")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(null, "Invalid credentials")));
    }
}
