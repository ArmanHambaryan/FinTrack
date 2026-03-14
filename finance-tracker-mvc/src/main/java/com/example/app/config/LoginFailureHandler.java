package com.example.app.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import service.UserService;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private final UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        if (exception.getCause() instanceof LockedException ||
                exception instanceof LockedException) {
            response.sendRedirect("/loginPage?blocked");
            return;
        }

        String email = request.getParameter("username");
        userService.findByEmail(email).ifPresent(user -> {
            if (user.is_blocked() && user.getBlocked_until() != null &&
                    user.getBlocked_until().isAfter(LocalDateTime.now())) {
                return;
            }
            int attempts = user.getLogin_attempts() + 1;
            user.setLogin_attempts(attempts);
            if (attempts >= 3) {
                user.set_blocked(true);
                user.setBlocked_until(LocalDateTime.now().plusHours(1));
                user.setLogin_attempts(0);
            }
            userService.update(user);
        });
        response.sendRedirect("/loginPage?error");
    }
}