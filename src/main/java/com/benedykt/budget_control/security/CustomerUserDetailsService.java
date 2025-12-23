package com.benedykt.budget_control.security;

import com.benedykt.budget_control.auth_users.entity.User;
import com.benedykt.budget_control.auth_users.repo.UserRepo;
import com.benedykt.budget_control.exceptions.NotFoudException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new NotFoudException(String.format("Username %s not found", username)));
        return AuthUser.builder()
                .user(user)
                .build();
    }
}
