package com.example.login_system.domain.port;

import com.example.login_system.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Integer id);
    User save(User user);
    void delete(User user);
    boolean existsByEmail(String email);
}
