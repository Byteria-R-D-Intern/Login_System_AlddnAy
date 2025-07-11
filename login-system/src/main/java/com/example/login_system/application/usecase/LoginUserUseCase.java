package com.example.login_system.application.usecase;

import com.example.login_system.domain.model.User;
import com.example.login_system.domain.port.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import com.example.login_system.application.service.*;

@Service
public class LoginUserUseCase {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passEncoder;

    public LoginUserUseCase(UserRepository userRepository, TokenService tokenService, PasswordEncoder passEncoder) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passEncoder = passEncoder;
    }

    public Optional<String> execute(String email, String password) {
        Optional<User> kullanici = userRepository.findByEmail(email);
        if (kullanici.isEmpty()) {
            return Optional.empty();
        }

        User user = kullanici.get();

        if (!passEncoder.matches(password, user.getPassword())) {
            return Optional.empty();
        }

        String token = tokenService.createToken(String.valueOf(user.getId()), user.getRole().toString());
        return Optional.of(token);
    }
    
    
}
