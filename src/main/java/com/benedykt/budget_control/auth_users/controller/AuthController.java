package com.benedykt.budget_control.auth_users.controller;

import com.benedykt.budget_control.auth_users.dtos.*;
import com.benedykt.budget_control.auth_users.services.AuthService;
import com.benedykt.budget_control.config.CookieProperties;
import com.benedykt.budget_control.config.JwtKeyProperties;
import com.benedykt.budget_control.res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;

import java.time.Duration;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtKeyProperties jwtKeyProperties;
    private final CookieProperties cookieProperties;

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken){

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .path("/")
                .maxAge(Duration.ofMillis(jwtKeyProperties.refreshExpiration()))
                .sameSite(cookieProperties.getSameSite())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @PostMapping("/register")
    public ResponseEntity<Response<RegistrationResponse>> register(@RequestBody @Valid RegistrationRequest registrationRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            HttpServletResponse httpServletResponse
    ){
        TokenPair tokens= authService.login(loginRequest);

        addRefreshTokenCookie(httpServletResponse,tokens.refreshToken());

        LoginResponse response = LoginResponse.builder()
                .userId(tokens.userId())
                .email(tokens.email())
                .accessToken(tokens.accessToken())
                .roles(tokens.roles())
                .build();

        Response<LoginResponse> body = Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login Successful")
                .data(response)
                .build();

        return ResponseEntity.ok(body);

    }
    @PostMapping("/refresh")
    public ResponseEntity<Response<RefreshTokenResponse>> refreshToken(
            @RequestBody @Valid String refreshToken,
            HttpServletResponse httpServletResponse
    ){
        TokenPair tokens= authService.refreshToken(refreshToken);

        addRefreshTokenCookie(httpServletResponse,tokens.refreshToken());

        RefreshTokenResponse response = RefreshTokenResponse.builder()
                .accessToken(tokens.accessToken())
                .build();

        Response<RefreshTokenResponse> body = Response.<RefreshTokenResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("AccessToken refreshed  successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(body);
    }

//
//    @PostMapping("/forgot-password")
//    public ResponseEntity<Response<?>> forgetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
//
//        return ResponseEntity.ok(authService.forgetPassword(resetPasswordRequest.getEmail()));
//    }
//
//    @PostMapping("/reset-password")
//    public ResponseEntity<Response<?>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
//
//        return ResponseEntity.ok(authService.updatePasswordViaResetCode(resetPasswordRequest));
//    }
}
