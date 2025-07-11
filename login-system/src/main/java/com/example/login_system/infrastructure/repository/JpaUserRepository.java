package com.example.login_system.infrastructure.repository;

import com.example.login_system.domain.model.User;
import com.example.login_system.domain.port.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Integer>, UserRepository {
    Optional<User> findByEmail(String email);
}