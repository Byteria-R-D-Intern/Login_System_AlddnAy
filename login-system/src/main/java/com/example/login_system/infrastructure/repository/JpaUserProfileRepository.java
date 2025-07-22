package com.example.login_system.infrastructure.repository;

import com.example.login_system.domain.model.UserProfile;
import com.example.login_system.domain.port.UserProfileRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserProfileRepository extends JpaRepository<UserProfile, Integer>, UserProfileRepository {
    Optional<UserProfile> findByUserId(Integer userId);
    Optional<UserProfile> findById(Integer id);
    boolean existsByUserId(Integer userId); 
}
