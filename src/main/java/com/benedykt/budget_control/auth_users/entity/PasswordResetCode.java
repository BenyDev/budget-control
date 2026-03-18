package com.benedykt.budget_control.auth_users.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "password_reset_codes")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    @OneToOne(targetEntity = AppUser.class,fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private AppUser appUser;

    private LocalDateTime expiryDate;
    private boolean used;


}
