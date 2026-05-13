package com.benedykt.budget_control.payment.entity;

import com.benedykt.budget_control.auth_users.entity.AppUser;
import com.benedykt.budget_control.enums.EPaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "payments")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    private EPaymentMethod paymentMethod;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private AppUser payer;
}
