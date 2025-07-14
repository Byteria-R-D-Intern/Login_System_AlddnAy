package com.example.login_system.application.usecase;

import com.example.login_system.domain.model.UserProfile;
import com.example.login_system.domain.port.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UpdateUserProfileUseCase {
    private final UserProfileRepository userProfileRepository;

    public UpdateUserProfileUseCase(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public Optional<UserProfile> execute(Integer userId, String address, String phone, LocalDate birthDate) {
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
}
