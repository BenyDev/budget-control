package com.benedykt.budget_control.auth_users.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Table(name = "refresh_tokens")
@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "TEXT",  nullable = false)
    private String token;

    private Instant expiryDate;

    @OneToOne
    private AppUser appUser;
}
