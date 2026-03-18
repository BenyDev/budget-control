package com.benedykt.budget_control.fixed_payment.entity;

import com.benedykt.budget_control.auth_users.entity.AppUser;
import com.benedykt.budget_control.category.entity.Category;
import com.benedykt.budget_control.subcategories.entity.Subcategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fixed_payments")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FixedPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "day_of_month",nullable = false)
    private int dayOfMonth;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "last_generated_year_mont", length=7)
    private String lastGeneratedYearMonth;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id", nullable = false )
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "subcategory_id", nullable = false )
    private Subcategory subcategory;

    @AssertTrue(message = "FixedPayment must reference exactly one : category or subcategory")
    public boolean isCategoryXorSubcategory(){
        return (category != null) ^ (subcategory != null);
    }





}
