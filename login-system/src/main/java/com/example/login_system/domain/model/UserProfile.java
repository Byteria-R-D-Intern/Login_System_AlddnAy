package com.example.login_system.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "user_profile",
    uniqueConstraints = @UniqueConstraint(columnNames = "user_id")
)
@Data
@NoArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String address;
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    public UserProfile(User user, String address, String phone, LocalDate birthDate) {
        this.user = user;
        this.address = address;
        this.phone = phone;
        this.birthDate = birthDate;
    }
}
