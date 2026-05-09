package service.impl;

import dto.UserDto;
import mapper.UserMapper;
import model.User;
import model.UserRole;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private INotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl service;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void registerEncodesPasswordBeforeSaving() {
        User user = new User();
        user.setPassword("plain");
        when(passwordEncoder.encode("plain")).thenReturn("encoded");

        service.register(user);

        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded", userCaptor.getValue().getPassword());
    }

    @Test
    void incrementLoginAttemptsBlocksUserAfterThirdFailure() {
        User user = new User();
        user.setId(3);
        user.setLogin_attempts(2);

        when(userRepository.findById(3)).thenReturn(Optional.of(user));

        service.incrementLoginAttempts(3);

        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertEquals(0, saved.getLogin_attempts());
        assertEquals(true, saved.is_blocked());
    }

    @Test
    void registerUserMapsPersistsAndSendsWelcomeEmail() {
        UserDto dto = new UserDto(null, "name", "user@example.com", "secret", UserRole.USER);
        User mapped = new User();
        mapped.setUsername("name");
        mapped.setEmail("user@example.com");

        when(userMapper.toEntity(dto)).thenReturn(mapped);
        when(userRepository.save(mapped)).thenReturn(mapped);

        service.registerUser(dto);

        verify(notificationService).sendEmail("user@example.com", "Welcome!", "name");
    }
}
