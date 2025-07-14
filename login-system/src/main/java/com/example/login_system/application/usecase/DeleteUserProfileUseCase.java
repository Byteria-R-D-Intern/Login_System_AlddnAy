package com.example.login_system.application.usecase;

import com.example.login_system.domain.port.UserProfileRepository;
import com.example.login_system.domain.model.UserProfile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeleteUserProfileUseCase {
    private final UserProfileRepository userProfileRepository;

    public DeleteUserProfileUseCase(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public boolean execute(Integer userId) {
        Optional<UserProfile> userProfileOpt = userProfileRepository.findById(userId);
        if (userProfileOpt.isEmpty()) {
            return false;
        }
        userProfileRepository.delete(userProfileOpt.get());
        return true;
    }
}
