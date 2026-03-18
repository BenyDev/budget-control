package com.benedykt.budget_control.auth_users.dtos;

import lombok.Builder;

@Builder
public record RefreshTokenResponse(
        String accessToken
)
{}