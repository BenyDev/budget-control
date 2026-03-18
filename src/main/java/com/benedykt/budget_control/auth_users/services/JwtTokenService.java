package com.benedykt.budget_control.auth_users.services;

import com.benedykt.budget_control.config.JwtKeyProperties;
import com.benedykt.budget_control.role.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwtKeyProperties jwtKeyProperties;

    public String generateAccessToken(Long userId, String email, Collection<Role> roles) {
        return generateToken(
                userId,
                email,
                roles,
                jwtKeyProperties.accessExpiration(),
                "ACCESS"
        );
    }

    public String generateRefreshToken(Long userId, String email, Collection<Role> roles) {
        return generateToken(
                userId,
                email,
                roles,
                jwtKeyProperties.refreshExpiration(),
                "REFRESH"
        );
    }

    public Instant getExpirationTimeFromToken(String token){

        Jwt jwt = jwtDecoder.decode(token);

        return jwt.getExpiresAt();

    }

    public Jwt decodeToken(String token) {
        return jwtDecoder.decode(token);
    }

    private String generateToken(
            Long userId,
            String email,
            Collection<Role> roles,
            long expirationMillis,
            String tokenType
    ) {
        Instant now = Instant.now();

        List<String> roleNames = roles.stream()
                .map(role -> "ROLE_" + role.getName().name())
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("budget-control")
                .subject(email)
                .issuedAt(now)
                .expiresAt(now.plusMillis(expirationMillis))
                .claim("userId", userId)
                .claim("roles", roleNames)
                .claim("tokenType", tokenType)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(() -> "RS256").build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims))
                .getTokenValue();
    }
}