package com.benedykt.budget_control.role.repo;

import com.benedykt.budget_control.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String roleName);
    boolean existsByName(String roleName);
}
