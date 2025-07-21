package com.example.login_system.api.controller;

import com.example.login_system.api.dto.*;
import com.example.login_system.application.usecase.UserProfileUseCase;
import com.example.login_system.domain.model.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import com.example.login_system.infrastructure.config.RequiredRole;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserDetailsController {

    private String validateUserDetailsRequest(UserDetailsRequest request) {
        if (request.getAddress() == null || request.getAddress().isBlank()) {
            return "Address cannot be empty";
        }
        if (request.getPhone() == null || !request.getPhone().matches("\\d{10}")) {
            return "Phone must be 10 digits";
        }
        if (request.getBirthDate() == null || !request.getBirthDate().isBefore(java.time.LocalDate.now())) {
            return "Birth date must be in the past";
        }
        return null;
    }

    private final UserProfileUseCase userProfileUseCase;

    public UserDetailsController(UserProfileUseCase userProfileUseCase) {
        this.userProfileUseCase = userProfileUseCase;
    }

    @Operation(summary = "Test Amaçlı", description = "ENdpointlar çalışıyor mu kontrol etmek için")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test endpoint works!");
    }

    @Operation(summary = "Kullanıcı detaylarını getirir", description = "JWT token ile kullanıcıya ait profil bilgilerini döner.")
    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getPrincipal();

        Optional<UserProfile> userProfile = userProfileUseCase.get(userId);
        if (userProfile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User details not found for user ID: " + userId);
        }
        UserProfile profile = userProfile.get();
        UserProfileResponse response = new UserProfileResponse(
            profile.getAddress(),
            profile.getPhone(),
            profile.getBirthDate()
        );
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Kullanıcı detaylarını oluşturur", description = "JWT token ile kullanıcıya ait profil bilgilerini oluşturur.")
    @PostMapping("/details")
    public ResponseEntity<?> createUserDetails(@RequestBody UserDetailsRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getPrincipal();

        String validationError = validateUserDetailsRequest(request);
        if (validationError != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
        }

        Optional<UserProfile> created = userProfileUseCase.create(userId, request.getAddress(), request.getPhone(), request.getBirthDate());
        if (created.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User details already exist or user not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created.get());
    }

    @Operation(summary = "Kullanıcı detaylarını günceller", description = "JWT token ile kullanıcıya ait profil bilgileri günceller.")
    @PutMapping("/details")
    public ResponseEntity<?> updateUserDetails(@RequestBody UserDetailsRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getPrincipal();

        String validationError = validateUserDetailsRequest(request);
        if (validationError != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
        }

        Optional<UserProfile> updated = userProfileUseCase.update(userId, request.getAddress(), request.getPhone(), request.getBirthDate());
        if (updated.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found");
        }
        return ResponseEntity.ok(updated.get());
    }

    @Operation(summary = "Kullanıcı detaylarını siler", description = "JWT token ile kullanıcıya ait profil bilgilerini siler.")
    @DeleteMapping("/details")
    @RequiredRole("ADMIN")
    public ResponseEntity<?> deleteUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getPrincipal();

        boolean deleted = userProfileUseCase.delete(userId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found");
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Sadece test amaçlı", description = "Token ın içindeki id ve role göre işlem yapılıyor.Role ADMIN değilse message gönderiyor.")
    @RequiredRole("ADMIN")
    @GetMapping("/admin/test")
    public ResponseEntity<?> adminTest() {
        return ResponseEntity.ok("Admin endpoint reached successfully!");
    }
}
