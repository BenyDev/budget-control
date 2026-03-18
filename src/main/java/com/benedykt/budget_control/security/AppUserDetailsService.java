package com.benedykt.budget_control.security;

import com.benedykt.budget_control.auth_users.entity.AppUser;
import com.benedykt.budget_control.auth_users.repo.UserRepo;
import com.benedykt.budget_control.exceptions.NotFoudException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {



        AppUser user = userRepo.findByEmail(username)
                .orElseThrow(() -> new NotFoudException(
                        String.format("User with name %s not found", username)
                ));

        Set<SimpleGrantedAuthority> authorities = user
                .getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());

        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities).build();

    }
}
