package com.example.rest.controller;

import jakarta.validation.Valid;
import model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.example.rest.dto.AuthRequest;
import com.example.rest.dto.AuthResponse;
import com.example.rest.dto.UserRestDto;
import com.example.rest.security.JwtService;
import com.example.rest.service.CustomUserDetailsService;
import com.example.rest.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService customUserDetailsService,
                          JwtService jwtService,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return authenticate(request);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<AuthResponse> loginForm(@RequestParam String email,
                                                  @RequestParam String password) {
        return authenticate(new AuthRequest(email, password));
    }

    private ResponseEntity<AuthResponse> authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException ex) {
            userService.findByEmail(request.email()).ifPresent(user -> userService.incrementLoginAttempts(user.getId()));
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        } catch (LockedException | DisabledException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);

        User user = userService.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        userService.resetLoginAttempts(user.getId());
        userService.updateLastActive(user.getEmail());

        UserRestDto userRestDto = new UserRestDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBalance(),
                user.isBlocked(),
                user.getCreated_at(),
                user.getUpdated_at(),
                user.getLastActive()
        );

        return ResponseEntity.ok(new AuthResponse(token, "Bearer", jwtService.getExpirationMs(), userRestDto));
    }
}
