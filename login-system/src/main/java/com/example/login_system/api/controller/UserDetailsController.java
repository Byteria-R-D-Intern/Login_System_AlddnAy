package com.example.login_system.api.controller;

import com.example.login_system.api.dto.*;
import com.example.login_system.application.service.TokenService;
import com.example.login_system.util.TokenUtil;
import com.example.login_system.application.usecase.UserProfileUseCase;
import com.example.login_system.domain.model.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

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
    private final TokenService tokenService;
    private final TokenUtil tokenUtil;
    
    public UserDetailsController(UserProfileUseCase userProfileUseCase, TokenService tokenService, TokenUtil tokenUtil) {
        this.userProfileUseCase = userProfileUseCase;
        this.tokenService = tokenService;
        this.tokenUtil = tokenUtil;
    }

    @Operation(summary = "Test Amaçlı", description = "ENdpointlar çalışıyor mu kontrol etmek için")
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test endpoint works!");
    }  
    
    @Operation(summary = "Kullanıcı detaylarını getirir", description = "JWT token ile kullanıcıya ait profil bilgilerini döner.")
    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            ResponseEntity<String> tokenError = tokenUtil.validateToken(authorizationHeader, tokenService);
            if (tokenError != null) {
                return tokenError;
            }
            String token = authorizationHeader.substring(7);
            Integer userId;
            try {
                userId = tokenService.extractUserIdFromToken(token);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token format");
            }
            
            Optional<UserProfile> userProfile = userProfileUseCase.get(userId);
            
            if (userProfile.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User details not found for user ID: " + userId);
            }
            
            UserProfile profile = userProfile.get();
            UserProfileResponse response = new UserProfileResponse(
                profile.getAddress(), // adres → address
                profile.getPhone(),   // tel → phone
                profile.getBirthDate() 
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error occurred");
        }
    }
        @Operation(summary = "Kullanıcı detaylarını oluşturur", description = "Admin istediği kullanıcı için, user ise sadece kendi için profil oluşturur.")
        @PostMapping("/details")
        public ResponseEntity<?> createUserDetails(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserDetailsRequest request) {

            // 1. Token ve Rol Kontrolü
            ResponseEntity<String> tokenError = tokenUtil.validateToken(authorizationHeader, tokenService);
            if (tokenError != null) {
                return tokenError;
            }
            String token = authorizationHeader.substring(7);
            String role = tokenService.extractUserRoleFromToken(token);
            Integer idFromToken = tokenService.extractUserIdFromToken(token);

            // 2. Hedef Kullanıcı ID'sini Belirle
            Integer targetUserId;
            if ("ADMIN".equals(role)) {
                if (request.getUserId() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Admin must provide userId to create a profile");
                }
                targetUserId = request.getUserId();
            } else {
                targetUserId = idFromToken;
            }

            // 3. Validasyon
            String validationError = validateUserDetailsRequest(request);
            if (validationError != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
            }

            // 4. Use Case'i Çağır
            Optional<UserProfile> created = userProfileUseCase.create(targetUserId, request.getAddress(), request.getPhone(), request.getBirthDate());
            if (created.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User profile already exists or user not found");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(created.get());
    }
    @Operation(summary = "Kullanıcı detaylarını günceller", description = "JWT token ile kullanıcıya ait profil bilgileri günceller.")
    @PutMapping("/details")
    public ResponseEntity<?> updateUserDetails(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody UserDetailsRequest request) {
    
        ResponseEntity<String> tokenError = tokenUtil.validateToken(authorizationHeader, tokenService);
        if (tokenError != null) {
            return tokenError;
        }
        String token = authorizationHeader.substring(7);
        Integer idFromToken = tokenService.extractUserIdFromToken(token);
        String role = tokenService.extractUserRoleFromToken(token);
    
        // 2. Validasyon
        String validationError = validateUserDetailsRequest(request);
        if (validationError != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
        }
    
        Integer targetUserId;
        if ("ADMIN".equals(role)) {
            if (request.getUserId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Admin must provide userId to update");
            }
            targetUserId = request.getUserId();
        } else {
            targetUserId = idFromToken;
        }
    
        // 3. Sadece use case çağrılır
        Optional<UserProfile> updated = userProfileUseCase.update(targetUserId, request.getAddress(), request.getPhone(), request.getBirthDate());
        if (updated.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found");
        }
        return ResponseEntity.ok(updated.get());
    }

    @Operation(summary = "Kullanıcı detaylarını siler", description = "Sadece admin, istenen userId'ye ait profil bilgisini siler.")
    @DeleteMapping("/details")
    public ResponseEntity<?> deleteUserDetails(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody UserIdRequest userIdRequest) {

        ResponseEntity<String> tokenError = tokenUtil.validateToken(authorizationHeader, tokenService);
        if (tokenError != null) {
            return tokenError;
        }
        String token = authorizationHeader.substring(7);
        String role = tokenService.extractUserRoleFromToken(token);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can delete user details");
        }

        Integer userId = userIdRequest.getUserId();
        boolean deleted = userProfileUseCase.delete(userId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found");
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Sadece test amaçlı", description = "Token ın içindeki id ve role göre işlem yapılıyor.Role ADMIN değilse message gönderiyor.")
    @GetMapping("/admin/test")
    public ResponseEntity<?> adminTest(@RequestHeader("Authorization") String authorizationHeader) {
        ResponseEntity<String> tokenError = tokenUtil.validateToken(authorizationHeader, tokenService);
        if (tokenError != null) {
            return tokenError;
        }
        String token = authorizationHeader.substring(7);
        String role = tokenService.extractUserRoleFromToken(token);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can access this endpoint");
        }
        return ResponseEntity.ok("Admin endpoint reached successfully!");
    }
}
