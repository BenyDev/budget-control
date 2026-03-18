package com.benedykt.budget_control.notification.services;

import com.benedykt.budget_control.auth_users.entity.AppUser;
import com.benedykt.budget_control.notification.dtos.NotificationDTO;

public interface NotificationService {
    void sendEmail(NotificationDTO notificationDTO, AppUser appUser);
}
