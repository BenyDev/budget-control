package com.benedykt.budget_control.auth_users.services.impl;



import com.benedykt.budget_control.auth_users.dtos.*;
import com.benedykt.budget_control.auth_users.entity.AppUser;
import com.benedykt.budget_control.auth_users.entity.PasswordResetCode;
import com.benedykt.budget_control.auth_users.entity.RefreshToken;
import com.benedykt.budget_control.auth_users.repo.PasswordResetCodeRepo;
import com.benedykt.budget_control.auth_users.repo.RefreshTokenRepository;
import com.benedykt.budget_control.auth_users.repo.UserRepo;
import com.benedykt.budget_control.auth_users.services.AuthService;
import com.benedykt.budget_control.auth_users.services.CodeGenerator;
import com.benedykt.budget_control.auth_users.services.JwtTokenService;
import com.benedykt.budget_control.enums.AuthProvider;
import com.benedykt.budget_control.enums.UserRole;
import com.benedykt.budget_control.exceptions.BadRequestException;
import com.benedykt.budget_control.exceptions.NotFoudException;
import com.benedykt.budget_control.notification.dtos.NotificationDTO;
import com.benedykt.budget_control.notification.services.NotificationService;
import com.benedykt.budget_control.res.Response;
import com.benedykt.budget_control.role.entity.Role;
import com.benedykt.budget_control.role.repo.RoleRepo;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final RefreshTokenRepository  refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    private final JwtTokenService jwtTokenService;
    private final CodeGenerator codeGenerator;
    private final PasswordResetCodeRepo  passwordResetCodeRepo;




    @Override
    @Transactional
    public Response<RegistrationResponse> register(RegistrationRequest request) {

        String normalizedEmail = request.getEmail().toLowerCase();

        if(userRepo.findByEmail(normalizedEmail).isPresent()){
            throw new BadRequestException("Email already exists");
        }

        Role basicRole = roleRepo.findByName(UserRole.USER_BASIC)
                .orElseThrow(() -> new NotFoudException("Default role User_BASIC not found"));

        AppUser appUser = AppUser.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(basicRole))
                .authProvider(AuthProvider.LOCAL)
                .providerId(normalizedEmail)
                .emailVerified(false)
                .build();

        AppUser savedAppUser = userRepo.save(appUser);


        //TODO SEND A WELCOME EMAIL OF THE USER AND ACCOUNT DETAILS TO THE USERS EMAIL

        //SEND WELCOME EMAIL
//        Map<String,Object> vars = new HashMap<>();
//        vars.put("name", savedAppUser.getFirstName());
//
//        NotificationDTO notificationDTO = NotificationDTO.builder()
//                .recipient(savedAppUser.getEmail())
//                .subject("Welcome to Budget Control Application :)")
//                .templateName("welcome")
//                .templateVariables(vars)
//                .build();
//
//        notificationService.sendEmail(notificationDTO, savedAppUser);

        //SEND ACCOUNT CREATION/DETAILS EMAIL
//        Map<String,Object> accountVars = new HashMap<>();
//        accountVars.put("name",savedAppUesr.getFirstName());
//
//
//        NotificationDTO accountCreatedEmail = NotificationDTO.builder()
//                .recipient(savedAppUesr.getEmail())
//                .subject("Your new Bank Account has been created")
//                .templateName("account-created")
//                .templateVariables(accountVars)
//                .build();
//        notificationService.sendEmail(accountCreatedEmail, savedAppUesr);

        RegistrationResponse response = RegistrationResponse.builder()
                .id(savedAppUser.getId())
                .email(savedAppUser.getEmail())
                .build();


        return Response.<RegistrationResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("User registered successfully!")
                .data(response)
                .build();


    }

    @Override
    @Transactional
    public TokenPair login(LoginRequest loginRequest) {

        String email = loginRequest.email().trim().toLowerCase();
        String password = loginRequest.password();

        AppUser user = userRepo.findWithRolesByEmail(email)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        String accessToken = jwtTokenService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRoles()
        );
        String refreshToken = jwtTokenService.generateRefreshToken(
                user.getId(),
                user.getEmail(),
                user.getRoles()
        );

        RefreshToken refreshTokenInstance = refreshTokenRepository.findByAppUser(user)
                .orElse(
                        RefreshToken.builder()
                                .appUser(user)
                                .build()
                );
        refreshTokenInstance.setToken(refreshToken);
        refreshTokenInstance.setExpiryDate(
                jwtTokenService.getExpirationTimeFromToken(refreshToken)
        );

        refreshTokenRepository.save(refreshTokenInstance);

        log.info("User logged in successfully: {}", email);



        return TokenPair.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .roles(user.getRoles().stream()
                        .map(role -> "ROLE_" + role.getName().name()).toList())
                .build();

    }

    @Override
    @Transactional
    public TokenPair refreshToken(String refreshToken) {
        try {
            Jwt jwt = jwtTokenService.decodeToken(refreshToken);

            String tokenType = jwt.getClaimAsString("tokenType");
            if (!"REFRESH".equals(tokenType)) {
                throw new BadRequestException("Invalid refresh token");
            }

            RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new BadRequestException("Invalid or expired refresh token"));

            if(storedToken.getExpiryDate().isBefore(Instant.now())){
                refreshTokenRepository.delete(storedToken);
                throw new BadRequestException("Invalid or expired refresh token");
            }

            AppUser user = storedToken.getAppUser();

            String newAccessToken = jwtTokenService.generateAccessToken(
                    user.getId(),
                    user.getEmail(),
                    user.getRoles()
            );
            String newRefreshToken = jwtTokenService.generateRefreshToken(
                    user.getId(),
                    user.getEmail(),
                    user.getRoles()
            );
            storedToken.setToken(newRefreshToken);
            storedToken.setExpiryDate(jwtTokenService.getExpirationTimeFromToken(newRefreshToken));

            refreshTokenRepository.save(storedToken);

            return TokenPair.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .refreshToken(newRefreshToken)
                    .accessToken(newAccessToken)
                    .roles(user.getRoles().stream().map(role -> "ROLE_" + role.getName().name()).toList())
                    .build();


        } catch (JwtException ex) {
            throw new BadRequestException("Invalid or expired refresh token");
        }
    }

//    @Override
//    @Transactional
//    public Response<?> forgetPassword(String email) {
//
//        AppUser appUser = userRepo.findByEmail(email)
//                .orElseThrow(() -> new NotFoudException("AppUesr not found"));
//        passwordResetCodeRepo.deleteByUserId(appUser.getId());
//
//        String code = codeGenerator.generateUniqueCode();
//
//        PasswordResetCode resetCode = PasswordResetCode.builder()
//                .appUser(appUser)
//                .code(code)
//                .expiryDate(calculateExpireDate())
//                .used(false)
//                .build();
//
//        passwordResetCodeRepo.save(resetCode);
//
//        //send email rest link out
//        Map<String,Object> templateVars = new HashMap<>();
//        templateVars.put("name", appUser.getFirstName());
//        templateVars.put("resetLink", resetLink + code);
//
//        NotificationDTO notificationDTO = NotificationDTO.builder()
//                .recipient(appUser.getEmail())
//                .subject("Reset Password Code")
//                .templateName("password-reset")
//                .templateVariables(templateVars)
//                .build();
//
//        notificationService.sendEmail(notificationDTO, appUser);
//
//        return Response.builder()
//                .statusCode(HttpStatus.OK.value())
//                .message("Password reset code sent to your email")
//                .build();
//
//    }
//
//    @Override
//    @Transactional
//    public Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {
//        String code = resetPasswordRequest.getCode();
//        String newPassword = resetPasswordRequest.getNewPassword();
//
//        //find and validate code
//        PasswordResetCode resetCode = passwordResetCodeRepo.findByCode(code)
//                .orElseThrow(() -> new BadRequestException("Invalid reset code"));
//
//        // check expiration first
//        if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
//            passwordResetCodeRepo.delete(resetCode); // Clean up expired code
//            throw new BadRequestException("Invalid reset code");
//        }
//
//        AppUser appUser = resetCode.getAppUser();
//        appUser.setPassword(passwordEncoder.encode(newPassword));
//        userRepo.save(appUser);
//
//        //Delete the code immediately after successful use
//        passwordResetCodeRepo.delete(resetCode);
//
//        //Send confirmation email
//        Map<String,Object> templateVars = new HashMap<>();
//        templateVars.put("name", appUser.getFirstName());
//
//        NotificationDTO confirmationEmail = NotificationDTO.builder()
//                .recipient(appUser.getEmail())
//                .subject("Password Updated Successfully")
//                .templateName("password-update-confirmation")
//                .templateVariables(templateVars)
//                .build();
//
//        notificationService.sendEmail(confirmationEmail, appUser);
//
//        return Response.builder()
//                .statusCode(HttpStatus.OK.value())
//                .message("Password updated successfully")
//                .build();
//    }
//
//    private LocalDateTime calculateExpireDate() {
//        return LocalDateTime.now().plusHours(5);
//    }
}
