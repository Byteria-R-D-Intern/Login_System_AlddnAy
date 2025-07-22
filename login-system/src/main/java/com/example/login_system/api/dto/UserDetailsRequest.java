package com.example.login_system.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsRequest {
    private Integer userId;
    private String address;
    private String phone;
    private LocalDate birthDate;
    
}
