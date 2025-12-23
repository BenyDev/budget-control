package com.benedykt.budget_control.notification.repo;

import com.benedykt.budget_control.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<Notification,Long> {


}
