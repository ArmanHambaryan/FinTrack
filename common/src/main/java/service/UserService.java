package service;

import model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    User save(User user);

    User update(User user);

    Optional<User> findById(Integer id);

    Optional<User> findByEmail(String Email);

    void deleteById(Integer id);

    void register(User user);

    void blockUser(Integer userId);

    void unblockUser(Integer userId);

    void updateLastActive(String email);
}
