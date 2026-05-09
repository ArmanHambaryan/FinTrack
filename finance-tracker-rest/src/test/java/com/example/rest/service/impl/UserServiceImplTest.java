package com.example.rest.service.impl;

import com.example.rest.dto.UserRestDto;
import dto.UserDto;
import mapper.UserMapper;
import model.User;
import model.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import repository.PasswordResetTokenRepository;
import repository.UserRepository;
import com.example.rest.service.INotificationService;
import com.example.rest.service.RestDtoMapperService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

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

    @Mock
    private RestDtoMapperService restDtoMapperService;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void getAdminUsersResponseIncludesUsersAndHighIncomeUsers() {
        User allUser = new User();
        allUser.setId(1);
        allUser.setUsername("all");
        User richUser = new User();
        richUser.setId(2);
        richUser.setUsername("rich");

        UserRestDto allDto = new UserRestDto(1, "all", "all@example.com", UserRole.USER, 10.0, false, LocalDateTime.now(), null, null);
        UserRestDto richDto = new UserRestDto(2, "rich", "rich@example.com", UserRole.ADMIN, 500000.0, false, LocalDateTime.now(), null, null);

        when(userRepository.findAll()).thenReturn(List.of(allUser));
        when(userRepository.findByBalanceGreaterThan(300000.0)).thenReturn(List.of(richUser));
        when(restDtoMapperService.toUserDto(allUser)).thenReturn(allDto);
        when(restDtoMapperService.toUserDto(richUser)).thenReturn(richDto);

        LinkedHashMap<String, Object> response = service.getAdminUsersResponse(null);

        assertEquals(300000.0, response.get("highIncomeThreshold"));
        assertEquals(List.of(allDto), response.get("users"));
        assertEquals(List.of(richDto), response.get("highIncomeUsers"));
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
