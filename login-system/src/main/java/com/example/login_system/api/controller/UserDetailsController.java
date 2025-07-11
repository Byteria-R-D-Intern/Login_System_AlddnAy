package com.example.login_system.api.controller;

import com.example.login_system.api.dto.UserProfileResponse;
import com.example.login_system.application.service.TokenService;
import com.example.login_system.application.usecase.GetUserProfileUseCase;
import com.example.login_system.domain.model.UserProfile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserDetailsController {
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final TokenService tokenService;
    
    public UserDetailsController(GetUserProfileUseCase getUserProfileUseCase, TokenService tokenService) {
        this.getUserProfileUseCase = getUserProfileUseCase;
        this.tokenService = tokenService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test endpoint works!");
    }  
    
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
            // UserDetailsController.java'da validateToken kontrolünden sonra ekle:
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
            
            Optional<UserProfile> userProfile = getUserProfileUseCase.execute(userId);
            
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
}
