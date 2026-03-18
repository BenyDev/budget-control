package com.benedykt.budget_control.budget.entity;

import com.benedykt.budget_control.auth_users.entity.AppUser;
import com.benedykt.budget_control.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal budget;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "user_id",  nullable = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY,  optional = false)
    @JoinColumn(name = "category_id",  nullable = false)
    private Category category;


}