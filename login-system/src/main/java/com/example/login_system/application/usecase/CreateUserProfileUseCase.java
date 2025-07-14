package com.example.login_system.application.usecase;

import com.example.login_system.domain.model.User;
import com.example.login_system.domain.model.UserProfile;
import com.example.login_system.domain.port.UserProfileRepository;
import com.example.login_system.domain.port.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CreateUserProfileUseCase {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public CreateUserProfileUseCase(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    public Optional<UserProfile> execute(Integer userId, String address, String phone, LocalDate birthDate) {
        if (userProfileRepository.existsByUserId(userId)) {
            return Optional.empty();
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Optional.empty();
        }
        UserProfile profile = new UserProfile(userId, user, address, phone, birthDate);
        userProfileRepository.save(profile);
        return Optional.of(profile);
    }
}
