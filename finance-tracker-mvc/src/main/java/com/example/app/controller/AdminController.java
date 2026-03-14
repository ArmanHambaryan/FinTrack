package com.example.app.controller;

import model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
