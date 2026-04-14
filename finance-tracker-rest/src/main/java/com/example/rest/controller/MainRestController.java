package com.example.rest.controller;

import com.example.rest.dto.UserRestDto;
import com.example.rest.service.RestDtoMapperService;
import model.User;
import model.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.UserService;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/main")
public class MainRestController {

    private final UserService userService;
    private final RestDtoMapperService mapperService;

    public MainRestController(UserService userService, RestDtoMapperService mapperService) {
        this.userService = userService;
        this.mapperService = mapperService;
    }

    @GetMapping("/")
    public LinkedHashMap<String, Object> home() {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("message", "finance-tracker-rest is running");
        response.put("controllers", 11);
        response.put("mainEndpoint", "/api/main");
        return response;
    }

    @GetMapping
    public LinkedHashMap<String, Object> mainPage() {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Finance tracker REST main endpoint");
        response.put("registerEndpoint", "/api/main/register");
        response.put("usersEndpoint", "/api/users");
        return response;
    }

    @GetMapping("/success-login")
    public LinkedHashMap<String, Object> successLogin(@RequestParam String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("role", user.getRole().name());
        response.put("redirectTo", "ADMIN".equals(user.getRole().name()) ? "/api/admin/users" : "/api/users/" + user.getId() + "/dashboard");
        response.put("user", mapperService.toUserDto(user));
        return response;
    }

    @GetMapping("/login-page")
    public LinkedHashMap<String, Object> loginPage(@RequestParam(required = false) String msg) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("page", "login");
        response.put("message", msg);
        return response;
    }

    @GetMapping("/register-page")
    public LinkedHashMap<String, Object> registerPage(@RequestParam(required = false) String msg) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("page", "register");
        response.put("message", msg);
        return response;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRestDto> register(@ModelAttribute User registeredUser,
                                                @RequestParam(value = "role", required = false) String role) {
        if (userService.findByEmail(registeredUser.getEmail()).isPresent()) {
            throw new RuntimeException("Username already exists!");
        }
        if ("ADMIN".equalsIgnoreCase(role)) {
            registeredUser.setRole(UserRole.ADMIN);
        } else {
            registeredUser.setRole(UserRole.USER);
        }

        userService.register(registeredUser);
        User savedUser = userService.findByEmail(registeredUser.getEmail()).orElseThrow();
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperService.toUserDto(savedUser));
    }
}
