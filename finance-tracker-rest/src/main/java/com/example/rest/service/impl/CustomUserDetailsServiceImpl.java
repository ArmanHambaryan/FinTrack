package com.example.rest.service.impl;

import lombok.RequiredArgsConstructor;
import model.User;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.rest.service.CustomUserDetailsService;
import com.example.rest.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.is_blocked() && user.getBlocked_until() != null
                && user.getBlocked_until().isAfter(LocalDateTime.now())) {
            throw new LockedException("Your account is blocked until " + user.getBlocked_until());
        }

        if (user.isBlocked()) {
            throw new DisabledException("User is blocked");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}

