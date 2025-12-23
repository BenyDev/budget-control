package com.benedykt.budget_control.notification.dtos;

import com.benedykt.budget_control.auth_users.dtos.UserDTO;
import com.benedykt.budget_control.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDTO {

    private Long id;

    private String subject;
    @NotBlank(message = "Recipient is required")
    private String recipient;
    private String body;
    private NotificationType type;
    private UserDTO user;
    private LocalDateTime createAt;


    //For values/variables to be passed into email template to send

    private String templateName;
    private Map<String,Object> templateVariables;
}
