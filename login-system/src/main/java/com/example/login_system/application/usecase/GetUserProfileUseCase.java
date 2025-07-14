package com.example.login_system.application.usecase;

import com.example.login_system.domain.model.UserProfile;
import com.example.login_system.domain.port.UserProfileRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class GetUserProfileUseCase {
    private final UserProfileRepository userProfileRepository;
    
    public GetUserProfileUseCase(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }
    
    public Optional<UserProfile> execute(Integer userId) {
        return userProfileRepository.findById(userId);
    }
}
