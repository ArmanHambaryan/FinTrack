package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import model.Goal;
import model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import service.GoalService;
import service.UserService;

import java.util.List;

@Controller
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final UserService userService;

    @PostMapping
    public String addGoal(@ModelAttribute Goal goal, Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByEmail(username).orElse(null);

        if (user != null) {
            goal.setUserId(user.getId());
            goalService.createGoal(goal);
        }

        return "redirect:/user/home";
    }

    @GetMapping("/goals")
    public String goals(ModelMap modelMap) {
        List<Goal> goals =goalService.getAllGoals();
        modelMap.addAttribute("goals", goals);
        return "goals";
    }

}
