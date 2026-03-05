package service.impl;

import lombok.RequiredArgsConstructor;
import model.User;
import org.springframework.stereotype.Service;
import repository.UserRepositroy;
import service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositroy userRepositroy;

    @Override
    public List<User> findAll() {
        return userRepositroy.findAll();
    }

    @Override
    public User save(User user) {

        return userRepositroy.save(user);
    }

    @Override
    public User update(User user) {
        return userRepositroy.save(user);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepositroy.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String Email) {
        return userRepositroy.findByEmail(Email);
    }

    @Override
    public void deleteById(Integer id) {
        userRepositroy.deleteById(id);
    }
}
