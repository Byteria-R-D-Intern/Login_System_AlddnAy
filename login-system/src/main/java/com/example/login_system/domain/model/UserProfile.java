package com.example.login_system.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false) // kullanici_id → user_id
    private User user; // kullanici → user

    private String address; // adres → address
    private String phone; // tel → phone

    @Column(name = "birth_date") // dogum_gunu → birth_date
    private LocalDate birthDate; // dogumGunu → birthDate

}
