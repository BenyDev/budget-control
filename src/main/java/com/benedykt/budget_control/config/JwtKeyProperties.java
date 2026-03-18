package com.benedykt.budget_control.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;



@ConfigurationProperties(prefix = "app.security")
public record JwtKeyProperties(
        Resource privateKeyLocation,
        Resource publicKeyLocation,
        long accessExpiration,
        long refreshExpiration

) {
}