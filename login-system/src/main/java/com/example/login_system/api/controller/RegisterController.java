package com.example.login_system.api.controller;

import com.example.login_system.api.dto.RegisterRequest;
import com.example.login_system.api.dto.RegisterResponse;
import com.example.login_system.application.usecase.RegisterUserUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    private final RegisterUserUseCase registerUserUseCase;

    public RegisterController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        // Basit validasyon
        if (request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RegisterResponse(null, "Email and password cannot be empty"));
        }

        return registerUserUseCase.execute(request.getEmail(), request.getPassword())
                .map(token -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(new RegisterResponse(token, "Registration successful")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new RegisterResponse(null, "Email already in use")));
    }
}
