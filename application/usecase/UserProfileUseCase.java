package com.example.login_system.application.usecase;

import com.example.login_system.domain.model.User;
import com.example.login_system.domain.model.UserProfile;
import com.example.login_system.domain.port.UserProfileRepository;
import com.example.login_system.domain.port.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserProfileUseCase {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    public UserProfileUseCase(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    // CREATE
    public Optional<UserProfile> create(Integer userId, String address, String phone, LocalDate birthDate) {
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

    // READ
    public Optional<UserProfile> get(Integer userId) {
        return userProfileRepository.findById(userId);
    }

    // UPDATE
    public Optional<UserProfile> update(Integer userId, String address, String phone, LocalDate birthDate) {
        Optional<UserProfile> userProfileOpt = userProfileRepository.findById(userId);
        if (userProfileOpt.isEmpty()) {
            return Optional.empty();
        }
        UserProfile profile = userProfileOpt.get();
        profile.setAddress(address);
        profile.setPhone(phone);
        profile.setBirthDate(birthDate);
        userProfileRepository.save(profile);
        return Optional.of(profile);
    }

    // DELETE
    public boolean delete(Integer userId) {
        Optional<UserProfile> userProfileOpt = userProfileRepository.findById(userId);
        if (userProfileOpt.isEmpty()) {
            return false;
        }
        userProfileRepository.delete(userProfileOpt.get());
        return true;
    }
}