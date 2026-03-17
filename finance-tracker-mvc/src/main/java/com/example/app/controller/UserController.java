package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import model.Goal;
import model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.GoalService;
import service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GoalService goalService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/user/home")
    public String userHome(Model model, Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByEmail(username).orElse(null);

        List<Goal> goals = (user == null) ? List.of() : goalService.findByUserId(user.getId());

        model.addAttribute("goals", goals);

        return "userHome";
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "adminHome";
    }


    @GetMapping("/users/delete/{id}")
    public String deleteUserById(@PathVariable int id) {
        userService.deleteById(id);
        return "redirect:/adminHome";
    }
    @GetMapping("/forgotPassword") // Փոխեցի, որ համապատասխանի Security-ին
    public String passwordForgetPage() {
        return "forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String handlePasswordForget(@RequestParam String email,
                                       @RequestParam String oldPassword,
                                       @RequestParam String newPassword,
                                       ModelMap modelMap) {

        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            modelMap.addAttribute("error", "User not found.");
            return "forgotPassword";
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            modelMap.addAttribute("error", "Old password is incorrect.");
            return "forgotPassword";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

        return "redirect:/loginPage?msg=Password updated successfully";
    }
}
