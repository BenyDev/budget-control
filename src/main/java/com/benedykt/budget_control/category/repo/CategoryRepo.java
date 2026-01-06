package com.benedykt.budget_control.category.repo;

import com.benedykt.budget_control.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
    boolean existsByName(String name);
}
