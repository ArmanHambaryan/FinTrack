package com.example.app.controller;


import lombok.RequiredArgsConstructor;
import model.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import repository.UserRepository;
import service.UserService;

import java.time.LocalDateTime;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/home")
    public String adminHome(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(required = false) String q,
                            Model model) {
        double highIncomeThreshold = 300000.0;
        List<User> highIncomeUsers = userRepository.findByBalanceGreaterThan(highIncomeThreshold);
        String searchQuery = q == null ? "" : q.trim();

        if (!searchQuery.isEmpty()) {
            List<User> users = userService.searchUsers(searchQuery);
            model.addAttribute("users", users);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
        } else {
            Page<User> usersPage = userService.getAllUsers(page);
            model.addAttribute("users", usersPage.getContent());
            model.addAttribute("currentPage", usersPage.getNumber());
            model.addAttribute("totalPages", usersPage.getTotalPages());
        }

        model.addAttribute("q", searchQuery);
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

