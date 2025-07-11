package com.example.login_system.api.dto;

import java.time.LocalDate;

public class UserProfileResponse {
    private String address; // adres → address
    private String phone;   // tel → phone
    private LocalDate birthDate; // dogumGunu → birthDate

    // Default constructor
    public UserProfileResponse() {}

    public UserProfileResponse(String address, String phone, LocalDate birthDate) {
        this.address = address;
        this.phone = phone;
        this.birthDate = birthDate;
    }

    // Getter ve Setter metodları
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
