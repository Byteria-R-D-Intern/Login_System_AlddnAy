package com.example.login_system.api.controller;


import com.example.login_system.application.usecase.LoginUserUseCase;
import com.example.login_system.api.dto.LoginRequest;
import com.example.login_system.api.dto.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginUserUseCase loginUserUseCase;

    public LoginController(LoginUserUseCase loginUserUseCase) {
        this.loginUserUseCase = loginUserUseCase;
        
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return loginUserUseCase.execute(request.getEmail(), request.getPassword())
                .map(token -> ResponseEntity.ok(new LoginResponse(token)))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

}
