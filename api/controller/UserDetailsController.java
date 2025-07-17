package com.example.login_system.api.controller;

import com.example.login_system.api.dto.*;
import com.example.login_system.application.service.TokenService;
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
    
    public UserDetailsController(UserProfileUseCase userProfileUseCase, TokenService tokenService) {
        this.userProfileUseCase = userProfileUseCase;
        this.tokenService = tokenService;
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
            // Token kontrolü
            if (authorizationHeader == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authorization header is required");
            }
            
            if (!authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid authorization format. Use 'Bearer <token>'");
            }
            
            String token = authorizationHeader.substring(7);
            
            if (token.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token cannot be empty");
            }
            
            if (!tokenService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }
            
            if (!tokenService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token");
            }

            // Token süresi kontrolü ekle
            if (tokenService.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token has expired");
            }
            
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
        @Operation(summary = "Kullanıcı detaylarını oluşturur", description = "JWT token ile kullanıcıya ait profil bilgilerini oluşturur.")
        @PostMapping("/details")
        public ResponseEntity<?> createUserDetails(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody UserDetailsRequest request) {

            // 1. Token kontrolü
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is required");
        }
            String token = authorizationHeader.substring(7);
            if (!tokenService.validateToken(token) || tokenService.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
            Integer userId = tokenService.extractUserIdFromToken(token);

            // 2. Validasyon
            String validationError = validateUserDetailsRequest(request);
            if (validationError != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
        }

            // 3. Kayıt var mı kontrolü
            Optional<UserProfile> created = userProfileUseCase.create(userId, request.getAddress(), request.getPhone(), request.getBirthDate());
            if (created.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User details already exist or user not found");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(created.get());
    }
    @Operation(summary = "Kullanıcı detaylarını günceller", description = "JWT token ile kullanıcıya ait profil bilgileri günceller.")
    @PutMapping("/details")
    public ResponseEntity<?> updateUserDetails(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody UserDetailsRequest request) {
    
        // 1. Token kontrolü
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is required");
        }
        String token = authorizationHeader.substring(7);
        if (!tokenService.validateToken(token) || tokenService.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
        Integer userId = tokenService.extractUserIdFromToken(token);
    
        // 2. Validasyon
        String validationError = validateUserDetailsRequest(request);
        if (validationError != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
        }
    
        // 3. Sadece use case çağrılır
        Optional<UserProfile> updated = userProfileUseCase.update(userId, request.getAddress(), request.getPhone(), request.getBirthDate());
        if (updated.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found");
        }
        return ResponseEntity.ok(updated.get());
    }

    @Operation(summary = "Kullanıcı detaylarını siler", description = "JWT token ile kullanıcıya ait profil bilgilerini siler.")
    @DeleteMapping("/details")
    public ResponseEntity<?> deleteUserDetails(
        @RequestHeader("Authorization") String authorizationHeader) {
    
        // 1. Token kontrolü
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is required");
        }
        String token = authorizationHeader.substring(7);
        if (!tokenService.validateToken(token) || tokenService.isTokenExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        //Role kontrolü
        String role = tokenService.extractUserRoleFromToken(token);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can delete user details");
        }

        Integer userId = tokenService.extractUserIdFromToken(token);
    
        // 2. Sadece use case çağrılır
        boolean deleted = userProfileUseCase.delete(userId);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found");
        }
    
        // 3. 204 No Content
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Sadece test amaçlı", description = "Token ın içindeki id ve role göre işlem yapılıyor.Role ADMIN değilse message gönderiyor.")
    @GetMapping("/admin/test")
    public ResponseEntity<?> adminTest(@RequestHeader("Authorization") String authorizationHeader) {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is required");
            }
            String token = authorizationHeader.substring(7);
            if (!tokenService.validateToken(token) || tokenService.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }
            String role = tokenService.extractUserRoleFromToken(token);
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only admin can access this endpoint");
            }
            return ResponseEntity.ok("Admin endpoint reached successfully!");
    }
}
