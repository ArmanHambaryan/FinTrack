package service.impl;

import model.PasswordResetToken;
import model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import repository.PasswordResetTokenRepository;
import repository.UserRepository;
import service.INotificationService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceImplTest {

    @Mock
    private INotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetTokenServiceImpl service;

    @Captor
    private ArgumentCaptor<PasswordResetToken> tokenCaptor;

    @Test
    void sendResetEmailCreatesTokenAndSendsNotification() {
        User user = new User();
        user.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        service.sendResetEmail("user@example.com");

        verify(passwordResetTokenRepository).save(tokenCaptor.capture());
        verify(notificationService).sendEmail(
                org.mockito.ArgumentMatchers.eq("user@example.com"),
                org.mockito.ArgumentMatchers.eq(SendEmailServiceImpl.RESET_PASSWORD_SUBJECT),
                org.mockito.ArgumentMatchers.contains("/reset-password/")
        );
        assertEquals(user, tokenCaptor.getValue().getUser());
    }

    @Test
    void getValidTokenDeletesExpiredToken() {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("expired");
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        when(passwordResetTokenRepository.findByToken("expired")).thenReturn(Optional.of(token));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.getValidToken("expired"));

        verify(passwordResetTokenRepository).delete(token);
        assertEquals("Token expired", exception.getMessage());
    }
}
