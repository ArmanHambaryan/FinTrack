package com.example.rest.controller;

import com.example.rest.dto.UserRestDto;
import model.User;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.rest.service.UserService;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/pageable")
public class PageableRestController {

    private final UserService userService;

    public PageableRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public LinkedHashMap<String, Object> getUsers(@RequestParam(defaultValue = "0") int page) {
        Page<User> usersPage = userService.getAllUsers(page);
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("users", usersPage.getContent().stream().map(user -> new UserRestDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBalance(),
                user.isBlocked(),
                user.getCreated_at(),
                user.getUpdated_at(),
                user.getLastActive())).toList());
        response.put("currentPage", usersPage.getNumber());
        response.put("totalPages", usersPage.getTotalPages());
        response.put("totalElements", usersPage.getTotalElements());
        return response;
    }
}
