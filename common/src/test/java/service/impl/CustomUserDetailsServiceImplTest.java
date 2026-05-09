package service.impl;

import model.User;
import model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailsServiceImpl service;

    @Test
    void loadUserByUsernameRejectsTemporarilyBlockedUser() {
        User user = new User();
        user.setEmail("blocked@example.com");
        user.set_blocked(true);
        user.setBlocked_until(LocalDateTime.now().plusMinutes(10));

        when(userService.findByEmail("blocked@example.com")).thenReturn(Optional.of(user));

        assertThrows(LockedException.class, () -> service.loadUserByUsername("blocked@example.com"));
    }

    @Test
    void loadUserByUsernameReturnsSecurityUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encoded");
        user.setRole(UserRole.ADMIN);

        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserByUsername("user@example.com");

        assertEquals("user@example.com", userDetails.getUsername());
        assertEquals("encoded", userDetails.getPassword());
        assertEquals("ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    }
}
