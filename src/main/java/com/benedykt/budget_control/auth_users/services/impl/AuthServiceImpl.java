package com.benedykt.budget_control.auth_users.services.impl;



import com.benedykt.budget_control.auth_users.dtos.LoginRequest;
import com.benedykt.budget_control.auth_users.dtos.LoginResponse;
import com.benedykt.budget_control.auth_users.dtos.RegistrationRequest;
import com.benedykt.budget_control.auth_users.dtos.ResetPasswordRequest;
import com.benedykt.budget_control.auth_users.entity.PasswordResetCode;
import com.benedykt.budget_control.auth_users.entity.User;
import com.benedykt.budget_control.auth_users.repo.PasswordResetCodeRepo;
import com.benedykt.budget_control.auth_users.repo.UserRepo;
import com.benedykt.budget_control.auth_users.services.AuthService;
import com.benedykt.budget_control.auth_users.services.CodeGenerator;
import com.benedykt.budget_control.enums.AccountType;
import com.benedykt.budget_control.enums.Currency;
import com.benedykt.budget_control.exceptions.BadRequestException;
import com.benedykt.budget_control.exceptions.NotFoudException;
import com.benedykt.budget_control.notification.dtos.NotificationDTO;
import com.benedykt.budget_control.notification.services.NotificationService;
import com.benedykt.budget_control.res.Response;
import com.benedykt.budget_control.role.entity.Role;
import com.benedykt.budget_control.role.repo.RoleRepo;
import com.benedykt.budget_control.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final NotificationService notificationService;

    private final CodeGenerator codeGenerator;
    private final PasswordResetCodeRepo  passwordResetCodeRepo;

    @Value("${password.reset.link}")
    private String resetLink;

    @Value("${database.initial.data.role-customer}")
    private String roleCustomer;

    @Override
    public Response<String> register(RegistrationRequest request) {
        List<Role> roles;

        if(request.getRoles()==null || request.getRoles().isEmpty()){
            //DEFAULT TO CUSTOMER
            Role role = roleRepo.findByName(roleCustomer)
                    .orElseThrow(() -> new NotFoudException(roleCustomer + " role not found"));
            roles = Collections.singletonList(role);

        }else {
            roles = request.getRoles().stream()
                    .map(roleName -> roleRepo.findByName(roleName)
                            .orElseThrow(() -> new NotFoudException("ROLE NOT FOUND" + roleName))).toList();

        }

        if(userRepo.findByEmail(request.getEmail()).isPresent()){
            throw new BadRequestException("Email already exists");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .active(true)
                .build();

        User savedUser = userRepo.save(user);


        //TODO SEND A WELCOME EMAIL OF THE USER AND ACCOUNT DETAILS TO THE USERS EMAIL

        //SEND WELCOME EMAIL
        Map<String,Object> vars = new HashMap<>();
        vars.put("name",savedUser.getFirstName());

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Welcome to Budget Control Application :)")
                .templateName("welcome")
                .templateVariables(vars)
                .build();

        notificationService.sendEmail(notificationDTO, savedUser);

        //SEND ACCOUNT CREATION/DETAILS EMAIL
//        Map<String,Object> accountVars = new HashMap<>();
//        accountVars.put("name",savedUser.getFirstName());
//
//
//        NotificationDTO accountCreatedEmail = NotificationDTO.builder()
//                .recipient(savedUser.getEmail())
//                .subject("Your new Bank Account has been created")
//                .templateName("account-created")
//                .templateVariables(accountVars)
//                .build();
//        notificationService.sendEmail(accountCreatedEmail, savedUser);

        return Response.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your account has benn created successfully")
                .data("Email of you account details has beed sent to your email.")
                .build();
    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoudException("Email not found"));

        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new BadRequestException("Passwords do not match");
        }

        String token = tokenService.generateToken(user.getEmail());

        LoginResponse loginResponse = LoginResponse.builder()
                .roles(user.getRoles().stream().map(Role::getName).toList())
                .token(token)
        .build();

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login Successful")
                .data(loginResponse)
                .build();
    }

    @Override
    @Transactional
    public Response<?> forgetPassword(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoudException("User not found"));
        passwordResetCodeRepo.deleteByUserId(user.getId());

        String code = codeGenerator.generateUniqueCode();

        PasswordResetCode resetCode = PasswordResetCode.builder()
                .user(user)
                .code(code)
                .expiryDate(calculateExpireDate())
                .used(false)
                .build();

        passwordResetCodeRepo.save(resetCode);

        //send email rest link out
        Map<String,Object> templateVars = new HashMap<>();
        templateVars.put("name", user.getFirstName());
        templateVars.put("resetLink", resetLink + code);

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Reset Password Code")
                .templateName("password-reset")
                .templateVariables(templateVars)
                .build();

        notificationService.sendEmail(notificationDTO, user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password reset code sent to your email")
                .build();

    }

    @Override
    @Transactional
    public Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {
        String code = resetPasswordRequest.getCode();
        String newPassword = resetPasswordRequest.getNewPassword();

        //find and validate code
        PasswordResetCode resetCode = passwordResetCodeRepo.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Invalid reset code"));

        // check expiration first
        if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetCodeRepo.delete(resetCode); // Clean up expired code
            throw new BadRequestException("Invalid reset code");
        }

        User user = resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        //Delete the code immediately after successful use
        passwordResetCodeRepo.delete(resetCode);

        //Send confirmation email
        Map<String,Object> templateVars = new HashMap<>();
        templateVars.put("name", user.getFirstName());

        NotificationDTO confirmationEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password Updated Successfully")
                .templateName("password-update-confirmation")
                .templateVariables(templateVars)
                .build();

        notificationService.sendEmail(confirmationEmail, user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password updated successfully")
                .build();
    }

    private LocalDateTime calculateExpireDate() {
        return LocalDateTime.now().plusHours(5);
    }
}
