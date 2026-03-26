package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import model.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.UserService;

@Controller
@RequiredArgsConstructor
public class PageableController {
    private final UserService userService;

    @GetMapping("/users")
    public String getUsers(@RequestParam (defaultValue = "0")
                               int page ,ModelMap modelMap) {

        Page<User> user = userService.getAllUsers(page);
        modelMap.addAttribute("users", user.getContent());
        modelMap.addAttribute("currentPage", user.getNumber());
        modelMap.addAttribute("totalPages", user.getTotalPages());
        modelMap.addAttribute("totalElements", user.getTotalElements());
        return "adminHome";
    }
}
