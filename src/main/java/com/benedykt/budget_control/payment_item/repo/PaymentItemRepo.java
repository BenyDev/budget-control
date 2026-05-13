package com.benedykt.budget_control.payment_item.repo;

import com.benedykt.budget_control.payment_item.entity.PaymentItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentItemRepo extends JpaRepository<PaymentItem, Long> {

}
