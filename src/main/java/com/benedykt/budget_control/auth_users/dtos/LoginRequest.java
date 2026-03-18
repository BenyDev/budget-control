package com.benedykt.budget_control.auth_users.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
public record LoginRequest (

    @Email
    @NotBlank(message = "Email is required")
     String email,

    @NotBlank(message = "Password is required")
     String password
){}
