package com.example.login_system.application.usecase;

import com.example.login_system.domain.model.User;
import com.example.login_system.domain.port.UserRepository;
import com.example.login_system.application.service.PasswordEncoder;
import com.example.login_system.application.service.TokenService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import com.example.login_system.domain.model.Role;

@Service
public class UserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    // Register
    public Optional<String> register(String email, String password, String roleStr) {
        if (userRepository.existsByEmail(email)) {
            return Optional.empty();
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);

        Role role;
        try {
            role = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Optional.empty(); // Geçersiz rol
        }
        user.setRole(role);

        userRepository.save(user);
        String token = tokenService.createToken(String.valueOf(user.getId()), user.getRole().toString());
        return Optional.of(token);
    }

    // Login
    public Optional<String> login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Optional.empty();
        }
        String token = tokenService.createToken(String.valueOf(user.getId()), user.getRole().toString());
        return Optional.of(token);
    }


}
