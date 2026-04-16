package com.example.rest.controller;

import com.example.rest.dto.UserRestDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import repository.UserRepository;
import com.example.rest.service.UserService;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserRepository userRepository;
    private final UserService userService;

    public AdminRestController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/users")
    public LinkedHashMap<String, Object> getUsers(@RequestParam(required = false) String q) {
        double highIncomeThreshold = 300000.0;
        List<UserRestDto> users = (q == null || q.isBlank())
                ? userService.findAll().stream().map(user -> new UserRestDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBalance(),
                user.isBlocked(),
                user.getCreated_at(),
                user.getUpdated_at(),
                user.getLastActive())).toList()
                : userService.searchUsers(q.trim()).stream().map(user -> new UserRestDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBalance(),
                user.isBlocked(),
                user.getCreated_at(),
                user.getUpdated_at(),
                user.getLastActive())).toList();

        List<UserRestDto> highIncomeUsers = userRepository.findByBalanceGreaterThan(highIncomeThreshold).stream()
                .map(user -> new UserRestDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole(),
                        user.getBalance(),
                        user.isBlocked(),
                        user.getCreated_at(),
                        user.getUpdated_at(),
                        user.getLastActive()))
                .toList();

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("users", users);
        response.put("highIncomeThreshold", highIncomeThreshold);
        response.put("highIncomeUsers", highIncomeUsers);
        return response;
    }

    @PostMapping("/block/{id}")
    public String blockUser(@PathVariable Integer id) {
        userService.blockUser(id);
        return "User blocked";
    }

    @PostMapping("/unblock/{id}")
    public String unblockUser(@PathVariable Integer id) {
        userService.unblockUser(id);
        return "User unblocked";
    }
}
