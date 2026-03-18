package com.benedykt.budget_control;

import com.benedykt.budget_control.auth_users.dtos.LoginRequest;
import com.benedykt.budget_control.auth_users.dtos.LoginResponse;
import com.benedykt.budget_control.auth_users.dtos.RegistrationRequest;
import com.benedykt.budget_control.auth_users.dtos.RegistrationResponse;
import com.benedykt.budget_control.auth_users.entity.AppUser;
import com.benedykt.budget_control.auth_users.repo.PasswordResetCodeRepo;
import com.benedykt.budget_control.auth_users.repo.UserRepo;
import com.benedykt.budget_control.auth_users.services.CodeGenerator;
import com.benedykt.budget_control.auth_users.services.JwtTokenService;
import com.benedykt.budget_control.auth_users.services.impl.AuthServiceImpl;
import com.benedykt.budget_control.enums.AuthProvider;
import com.benedykt.budget_control.enums.UserRole;
import com.benedykt.budget_control.exceptions.BadRequestException;
import com.benedykt.budget_control.notification.services.NotificationService;
import com.benedykt.budget_control.res.Response;
import com.benedykt.budget_control.role.entity.Role;
import com.benedykt.budget_control.role.repo.RoleRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificationService notificationService;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private CodeGenerator codeGenerator;

    @Mock
    private PasswordResetCodeRepo passwordResetCodeRepo;

    @InjectMocks
    private AuthServiceImpl authService;

    private Role basicRole;


    @BeforeEach
    void setUp() {
        basicRole = Role.builder()
                .id(1L)
                .name(UserRole.USER_BASIC)
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegistrationRequest request = RegistrationRequest.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .email("jan@test.com")
                .password("secret123")
                .build();

        when(userRepo.findByEmail("jan@test.com")).thenReturn(Optional.empty());
        when(roleRepo.findByName(UserRole.USER_BASIC)).thenReturn(Optional.of(basicRole));
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-pass");

        AppUser savedUser = AppUser.builder()
                .id(10L)
                .firstName("Jan")
                .lastName("Kowalski")
                .email("jan@test.com")
                .password("encoded-pass")
                .roles(List.of(basicRole))
                .authProvider(AuthProvider.LOCAL)
                .providerId("jan@test.com")
                .emailVerified(false)
                .build();

        when(userRepo.save(any(AppUser.class))).thenReturn(savedUser);

        Response<RegistrationResponse> response = authService.register(request);

        assertEquals(201, response.getStatusCode());
        assertEquals("User registered successfully!", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(10L, response.getData().id());
        assertEquals("jan@test.com", response.getData().email());

        ArgumentCaptor<AppUser> captor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepo).save(captor.capture());

        AppUser userToSave = captor.getValue();
        assertEquals("Jan", userToSave.getFirstName());
        assertEquals("Kowalski", userToSave.getLastName());
        assertEquals("jan@test.com", userToSave.getEmail());
        assertEquals("encoded-pass", userToSave.getPassword());
        assertEquals(AuthProvider.LOCAL, userToSave.getAuthProvider());
        assertEquals("jan@test.com", userToSave.getProviderId());
        assertFalse(userToSave.getEmailVerified());
        assertEquals(1, userToSave.getRoles().size());
        assertEquals(UserRole.USER_BASIC, userToSave.getRoles().getFirst().getName());
    }

    @Test
    void shouldThrowBadRequestWhenEmailAlreadyExistsDuringRegistration(){

        RegistrationRequest request = RegistrationRequest.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .email("jan@test.com")
                .password("secret123")
                .build();

        when(userRepo.findByEmail("jan@test.com")).thenReturn(Optional.of(AppUser.builder().build()));
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> authService.register(request)
        );
        assertEquals("Email already exists", ex.getMessage());
        verify(userRepo, never()).save(any(AppUser.class));

    }

    @Test
    void shouldLoginUserSuccessfully() {
//
//        LoginRequest request = LoginRequest.builder()
//                .email("jan@test.com")
//                .password("secret123")
//                .build();
//
//        AppUser userInDatabase = AppUser.builder()
//                .id(10L)
//                .firstName("Jan")
//                .lastName("Kowalski")
//                .email("jan@test.com")
//                .password("encoded-pass")
//                .roles(List.of(basicRole))
//                .authProvider(AuthProvider.LOCAL)
//                .providerId("jan@test.com")
//                .emailVerified(false)
//                .build();
//
//        when(userRepo.findByEmail("jan@test.com")).thenReturn(Optional.of(userInDatabase));
//        when(passwordEncoder.matches("secret123", "encoded-pass")).thenReturn(true);
//        when(jwtTokenService
//                .generateAccessToken(userInDatabase.getId(), "jan@test.com",userInDatabase.getRoles()))
//            .thenReturn("jwt-token");
//
//            Response<LoginResponse> response = authService.login(request); // TODO method .login return TokenPair not Response<LoginResponse>
//
//            assertEquals(200, response.getStatusCode());
//            assertEquals("Login successful", response.getMessage());
//            assertNotNull(response.getData());
//            assertEquals("jwt-token", response.getData().getAccessToken());
//            assertEquals(List.of("ROLE_USER_BASIC"), response.getData().getRoles());

    }

    @Test
    void shouldThrowBadRequestWhenEmailNotExistsDuringLogin(){

        LoginRequest request = LoginRequest.builder()
                .email("joe@test.com")
                .password("secret123")
                .build();

        when(userRepo.findWithRolesByEmail("joe@test.com")).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                ()-> authService.login(request)
        );
        assertEquals("Invalid email or password", ex.getMessage());

    }

    @Test
    void shouldThrowBadRequestWhenPasswordNotMatchDuringLogin(){

        LoginRequest request = LoginRequest.builder()
                .email("joe@test.com")
                .password("secret123")
                .build();

        AppUser user = AppUser.builder()
                .id(1L)
                .email("joe@test.com")
                .password("encoded-pass")
                .roles(List.of())
                .build();

        when(userRepo.findWithRolesByEmail("joe@test.com")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("secret123", "encoded-pass")).thenReturn(false);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                ()-> authService.login(request));

        assertEquals("Invalid email or password", ex.getMessage());

        verify(userRepo).findWithRolesByEmail("joe@test.com");
        verify(passwordEncoder).matches("secret123", "encoded-pass");
        verify(jwtTokenService, never()).generateAccessToken(anyLong(),anyString(),any());
        verify(jwtTokenService, never()).generateRefreshToken(anyLong(),anyString(),any());

    }
}