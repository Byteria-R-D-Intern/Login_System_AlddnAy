package com.example.login_system.application.usecase;

import com.example.login_system.domain.model.*;
import com.example.login_system.domain.port.UserRepository;
import com.example.login_system.application.service.PasswordEncoder;
import com.example.login_system.application.service.TokenService;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public Optional<String> execute(String email, String password) {
        // 1. Email daha önce kullanılmış mı?
        if (userRepository.existsByEmail(email)) {
            return Optional.empty();
        }

        // 2. Şifreyi hash’le
        String hashedPassword = passwordEncoder.encode(password);

        // 3. Yeni kullanıcıyı oluştur ve kaydet
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRole(Role.USER); // veya default rol

        userRepository.save(user);

        // 4. Token üret ve döndür
        String token = tokenService.createToken(String.valueOf(user.getId()), user.getRole().toString());
        return Optional.of(token);
    }
}
