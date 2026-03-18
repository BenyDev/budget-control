package com.benedykt.budget_control.budget_details.entity;

import com.benedykt.budget_control.budget.entity.Budget;
import com.benedykt.budget_control.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "budget_details")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BudgetDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal minExpense;
    private BigDecimal maxExpense;
    private BigDecimal priorityExpense;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "budget_id",  nullable = false)
    private Budget budget;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

}
