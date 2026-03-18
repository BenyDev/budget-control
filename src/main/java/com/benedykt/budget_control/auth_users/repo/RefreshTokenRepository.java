package com.benedykt.budget_control.auth_users.repo;

import com.benedykt.budget_control.auth_users.entity.AppUser;
import com.benedykt.budget_control.auth_users.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByAppUser(AppUser appUser);
}
