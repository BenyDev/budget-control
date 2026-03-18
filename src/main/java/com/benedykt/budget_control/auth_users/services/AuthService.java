package com.benedykt.budget_control.auth_users.services;


import com.benedykt.budget_control.auth_users.dtos.*;
import com.benedykt.budget_control.res.Response;

public interface AuthService {
    Response<RegistrationResponse> register(RegistrationRequest request);
    TokenPair login(LoginRequest loginRequest);
    TokenPair refreshToken(String refreshToken);
//    Response<?> forgetPassword(String email);
//    Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);
}
