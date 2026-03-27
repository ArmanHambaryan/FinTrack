package service.impl;

import dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import service.INotificationService;
import service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final INotificationService notificationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private static final int PAGE_SIZE = 5;


    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }


    @Override
    public User save(User user) {
     return  userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String Email) {
        return userRepository.findByEmail(Email);
    }

    @Override
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void blockUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBlocked(true);
        userRepository.save(user);
    }

    @Override
    public void unblockUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBlocked(false);
        userRepository.save(user);
    }

    @Override
    public void updateLastActive(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setUpdated_at(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    @Override
    public void block(Integer id, int hours) {
        userRepository.findById(id).ifPresent(user -> {
            user.set_blocked(true);
            user.setBlocked_until(LocalDateTime.now().plusHours(hours));
            userRepository.save(user);
        });
    }

    @Override
    public void incrementLoginAttempts(Integer id) {
        userRepository.findById(id).ifPresent(user -> {
            int attempts = user.getLogin_attempts() + 1;
            user.setLogin_attempts(attempts);
            if (attempts >= 3) {
                user.set_blocked(true);
                user.setBlocked_until(LocalDateTime.now().plusHours(1));
                user.setLogin_attempts(0);
            }
            userRepository.save(user);
        });
    }

    @Override
    public void resetLoginAttempts(Integer id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setLogin_attempts(0);
            userRepository.save(user);

        });
    }

    @Override
    public Page<User> getAllUsers(int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("id").ascending());
        return userRepository.findAll(pageable);
    }

    @Override
    public void registerUser(UserDto dto) {
        log.info("Registering user with email: {}", dto.getEmail());
        User user = userRepository.save(userMapper.toEntity(dto));
        log.info("User registered successfully with id: {} and email: {}", user.getId(), user.getEmail());

        notificationService.sendEmail(
                user.getEmail(),
                "Welcome!",
                user.getUsername()
        );
        log.info("Welcome email triggered for user: {}", user.getEmail());
    }

}
