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

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;
    private final UserService userService;

    @PostMapping
    public String addGoal(@RequestParam("name") String name,
                          @RequestParam("target_amount") double targetAmount,
                          @RequestParam(name = "currency_code", defaultValue = "AMD") String currencyCode,
                          @RequestParam(name = "deadline", required = false) String deadline,
                          Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByEmail(username).orElse(null);

        if (user != null) {
            Goal goal = new Goal();
            goal.setUserId(user.getId());
            goal.setName(name);
            goal.setTarget_amount(targetAmount);
            goal.setSaved_amount(0.0);
            goal.setCurrency_code(currencyCode);
            if (deadline != null && !deadline.isBlank()) {
                goal.setDeadline(java.time.LocalDate.parse(deadline));
            }
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

    @PostMapping("/delete/{id}")
    public String deleteGoal(@PathVariable Integer id, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByEmail(username).orElse(null);
        Goal goal = goalService.getGoalById(id);

        if (user != null && goal != null && user.getId().equals(goal.getUserId())) {
            goalService.deleteGoal(id);
        }

        return "redirect:/user/home";
    }
    @PostMapping("/{id}/progress")
    public String updateProgress(@PathVariable Integer id,
                                 @RequestParam("amount") BigDecimal amount,
                                 Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByEmail(username).orElse(null);
        Goal goal = goalService.getGoalById(id);

        if (user == null || goal == null || !user.getId().equals(goal.getUserId())) {
            return "redirect:/user/home";
        }
        if (amount == null || amount.signum() <= 0) {
            return "redirect:/user/home?goalMessage=" + encodeMessage("Amount must be greater than 0.");
        }

        try {
            goalService.updateProgress(id, amount.doubleValue());
        } catch (IllegalArgumentException e) {
            return "redirect:/user/home?goalMessage=" + encodeMessage(e.getMessage());
        }
        return "redirect:/user/home";
    }

    private String encodeMessage(String message) {
        return URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
}
