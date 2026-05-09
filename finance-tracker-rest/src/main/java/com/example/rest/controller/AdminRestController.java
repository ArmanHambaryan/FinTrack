package com.example.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.rest.service.UserService;

import java.util.LinkedHashMap;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final UserService userService;


    @GetMapping("/users")
    public LinkedHashMap<String, Object> getUsers(@RequestParam(required = false) String q) {
        return userService.getAdminUsersResponse(q);
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
