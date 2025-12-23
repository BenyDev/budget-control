package com.benedykt.budget_control.auth_users.services;


import com.benedykt.budget_control.auth_users.dtos.LoginRequest;
import com.benedykt.budget_control.auth_users.dtos.LoginResponse;
import com.benedykt.budget_control.auth_users.dtos.RegistrationRequest;
import com.benedykt.budget_control.auth_users.dtos.ResetPasswordRequest;
import com.benedykt.budget_control.res.Response;

public interface AuthService {
//    Response<String> register(RegistrationRequest request);
    Response<LoginResponse> login(LoginRequest loginRequest);
    Response<?> forgetPassword(String email);
    Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);
}
