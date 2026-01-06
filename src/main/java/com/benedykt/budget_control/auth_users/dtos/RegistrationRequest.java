package com.benedykt.budget_control.auth_users.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class RegistrationRequest {

    @NotBlank(message = "FirstName is required")
    private String firstName;

    @NotBlank(message = "LastName is required")
    private String lastName;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    private List<String> roles;

    @NotBlank(message = "Password is required")
    private String password;


}
