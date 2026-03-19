package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import model.Goal;
import model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import service.GoalService;
import service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GoalService goalService;

    @GetMapping("/user/home")
    public String userHome(Model model, Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByEmail(username).orElse(null);

        List<Goal> goals = (user == null) ? List.of() : goalService.findByUserId(user.getId());

        model.addAttribute("goals", goals);

        return "userHome";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUserById(@PathVariable int id) {
        userService.deleteById(id);
        return "redirect:/adminHome";
    }

}
