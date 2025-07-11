package com.example.login_system.domain.port;

import com.example.login_system.domain.model.UserProfile;
import java.util.Optional;

public interface UserProfileRepository {
    Optional<UserProfile> findByUserId(Integer userId); // kullaniciId → userId
    UserProfile save(UserProfile userProfile);
    void delete(UserProfile userProfile);
    boolean existsByUserId(Integer userId); // kullaniciId → userId
}
