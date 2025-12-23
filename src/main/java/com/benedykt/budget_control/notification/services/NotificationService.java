package com.benedykt.budget_control.notification.services;

import com.benedykt.budget_control.auth_users.entity.User;
import com.benedykt.budget_control.notification.dtos.NotificationDTO;

public interface NotificationService {
    void sendEmail(NotificationDTO notificationDTO, User user);
}
