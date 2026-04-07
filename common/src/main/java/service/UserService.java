package service;

import dto.UserDto;
import model.User;
import org.springframework.data.domain.Page;

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

    void registerUser(UserDto dto);

    void blockUser(Integer userId);

    void unblockUser(Integer userId);

    void updateLastActive(String email);
    void block(Integer id, int hours);

    void incrementLoginAttempts(Integer id);

    void resetLoginAttempts(Integer id);

    Page<User> getAllUsers(int page);

    List<User> searchUsers(String q);

}
