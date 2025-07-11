package com.example.login_system.application.service;

import java.util.Date;

public interface TokenService {
    String createToken(String id, String role);
    boolean validateToken(String token);
    Integer extractUserIdFromToken(String token);
    
    // Yeni metodlar
    boolean isTokenExpired(String token);
    Date getTokenExpirationDate(String token);
    long getTokenExpirationTimeInSeconds(String token);
}
