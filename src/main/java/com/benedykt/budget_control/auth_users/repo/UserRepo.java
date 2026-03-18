package com.benedykt.budget_control.auth_users.repo;

import com.benedykt.budget_control.auth_users.entity.AppUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    @EntityGraph(attributePaths = "roles")
    Optional<AppUser> findWithRolesByEmail(String email);
}
