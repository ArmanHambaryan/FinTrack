package service.impl;

import lombok.RequiredArgsConstructor;
import model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import service.SendEmailService;
import service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SendEmailService sendEmailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }



    @Override
    public User save(User user) {
        if (user.getEmail().contains("@")){
            sendEmailService.sendEmail(user.getEmail(),"Welcome to our platform",
                    "You have successfully registered. please login http://localhost:8082/loginPage");
        }

        return userRepository.save(user);
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
        userRepository.findById(id).ifPresent(user -> {user.set_blocked(true);
            user.setBlocked_until(LocalDateTime.now().plusHours(hours));
            userRepository.save(user);});
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
            userRepository.findById(id).ifPresent(user -> {user.setLogin_attempts(0);
                userRepository.save(user);

            });
    }

}
