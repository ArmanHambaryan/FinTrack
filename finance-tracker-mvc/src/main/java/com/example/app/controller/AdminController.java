package com.example.app.controller;


import model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import repository.UserRepository;
import service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;

    public AdminController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/home")
    public String adminHome(Model model) {
        List<User> users = userRepository.findAll();
        double highIncomeThreshold = 300000.0;
        List<User> highIncomeUsers = userRepository.findByBalanceGreaterThan(highIncomeThreshold);
        model.addAttribute("users", users);
        model.addAttribute("highIncomeUsers", highIncomeUsers);
        model.addAttribute("highIncomeThreshold", highIncomeThreshold);
        model.addAttribute("onlineCutoff", LocalDateTime.now().minusMinutes(5));
        return "adminHome";
    }

    @PostMapping("/block/{id}")
    public String blockUser(@PathVariable Integer id) {
        userService.blockUser(id);
        return "redirect:/admin/home";
    }

    @PostMapping("/unblock/{id}")
    public String unblockUser(@PathVariable Integer id) {
        userService.unblockUser(id);
        return "redirect:/admin/home";
    }
}
