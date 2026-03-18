package com.benedykt.budget_control.auth_users.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
public record RegistrationResponse (

      Long id,
      String email
){}
