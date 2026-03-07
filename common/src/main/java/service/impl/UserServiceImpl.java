package service.impl;

import lombok.RequiredArgsConstructor;
import model.User;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {

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
}
