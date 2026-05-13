package com.benedykt.budget_control.payment.repo;

import com.benedykt.budget_control.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
}
