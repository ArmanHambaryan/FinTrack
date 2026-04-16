package com.example.rest.service.impl;

import lombok.RequiredArgsConstructor;
import model.PasswordResetToken;
import model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.PasswordResetTokenRepository;
import repository.UserRepository;
import com.example.rest.service.INotificationService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl {
    private final INotificationService notificationService;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public void sendResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found " + email));

        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(PasswordResetToken.EXPIRY_MINUTES)
        );
        passwordResetTokenRepository.save(resetToken);

        String link = "http://localhost:8083/reset-password/" + token;
        notificationService.sendEmail(email, SendEmailServiceImpl.RESET_PASSWORD_SUBJECT, link);
    }

    public PasswordResetToken getValidToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found " + token));
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("Token expired");
        }
        return resetToken;
    }

    @CacheEvict(cacheNames = "users", allEntries = true)
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = getValidToken(token);
        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);
    }

}
