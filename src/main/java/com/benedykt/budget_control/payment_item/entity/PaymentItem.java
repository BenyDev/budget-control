package com.benedykt.budget_control.payment_item.entity;

import com.benedykt.budget_control.category.entity.Category;
import com.benedykt.budget_control.payment.entity.Payment;
import com.benedykt.budget_control.subcategories.entity.Subcategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "payment_items")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentItem {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "subcategory_id", nullable = true)
    private Subcategory subcategory;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment  payment;
}
