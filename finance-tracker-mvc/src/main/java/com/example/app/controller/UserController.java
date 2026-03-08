package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import service.UserService;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/home")
    public String userHome() {
        return "redirect:/userHome";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "redirect:/adminHome";
    }


    @GetMapping("/users/delete/{id}")
    public String deleteUserById(@PathVariable int id) {
        userService.deleteById(id);
        return "redirect:/adminHome";
    }


}
