package com.benedykt.budget_control;

import com.benedykt.budget_control.auth_users.services.JwtTokenService;
import com.benedykt.budget_control.config.JwtKeyProperties;
import com.benedykt.budget_control.enums.UserRole;
import com.benedykt.budget_control.role.entity.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
        "app.security.access-expiration=1000",
        "app.security.refresh-expiration=1000",
        "app.security.private-key-location=classpath:keys/private.pem",
        "app.security.public-key-location=classpath:keys/public.pem"
})
public class JwtTokenServiceTest {

    @Autowired
    private  JwtTokenService jwtTokenService;

    @Autowired
    private JwtKeyProperties jwtKeyProperties;

    @Test
    void shouldGenerateAccessToken(){

        List<Role> roles = List.of();

        String token = jwtTokenService.generateAccessToken(1L, "joe@test.com", roles);



        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldContainCorrectClaimsInAccessToken(){
        List<Role> roles = List.of();

        String token = jwtTokenService.generateAccessToken(1L, "joe@test.com", roles);

        Jwt jwt = jwtTokenService.decodeToken(token);

        assertEquals("joe@test.com",jwt.getSubject());
        assertEquals(1L,jwt.getClaims().get("userId"));
        assertEquals("ACCESS", jwt.getClaims().get("tokenType"));

    }

    @Test
    void shouldContainRolesInToken(){

        List<Role> roles = List.of(
                Role.builder()
                        .name(UserRole.USER_BASIC)
                        .build());

        String token = jwtTokenService.generateAccessToken(
                1L,
                "joe@test.com",
                roles
        );

        Jwt jwt = jwtTokenService.decodeToken(token);

        List<String> tokenRoles = jwt.getClaim("roles");

        assertTrue(tokenRoles.contains("ROLE_" + UserRole.USER_BASIC.name()));

    }

    @Test
    void shouldGeneratedRefreshTokenWithCorrectType(){

        List<Role> roles = List.of();

        String token = jwtTokenService.generateRefreshToken(
                1L,
                "joe@test.com",
                roles
        );

        Jwt jwt = jwtTokenService.decodeToken(token);

        assertEquals("REFRESH", jwt.getClaim("tokenType").toString());
    }

    @Test
    void shouldSetExpirationCorrectly(){

        String token = jwtTokenService.generateAccessToken(
                1L, "joe@test.com", List.of()
        );
        Jwt jwt = jwtTokenService.decodeToken(token);

        Instant issuedAt = jwt.getIssuedAt();
        Instant expiresAt = jwt.getExpiresAt();

        assertNotNull(issuedAt);
        assertNotNull(expiresAt);

        assertTrue(expiresAt.isAfter(issuedAt));

    }

    @Test
    void shouldSetCorrectExpirationTimeForAccessToken(){

        String token = jwtTokenService.generateAccessToken(
                1L, "joe@test.com", List.of()
        );

        Jwt jwt = jwtTokenService.decodeToken(token);

        Instant issuedAt = jwt.getIssuedAt();
        Instant expiresAt = jwt.getExpiresAt();

        long expected = jwtKeyProperties.accessExpiration();
        long actual = expiresAt.toEpochMilli() -  issuedAt.toEpochMilli();

        assertNotNull(issuedAt);
        assertNotNull(expiresAt);

        assertTrue(Math.abs(actual - expected) < 1000);

    }

}
