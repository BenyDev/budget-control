package com.benedykt.budget_control.auth_users.dtos;

import lombok.Builder;

import java.util.List;

@Builder
public record TokenPair(String accessToken,
                        String refreshToken,
                        Long userId,
                        String email,
                        List<String> roles
                        ) {}
