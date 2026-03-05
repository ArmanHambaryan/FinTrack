package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import model.Goal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.GoalService;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @GetMapping
    public List<Goal> getGoals() {
        return goalService.getAllGoals();
    }

    @GetMapping("/{id}")
    public Goal getGoal(@PathVariable Integer id) {
        return goalService.getGoalById(id);
    }

    @PostMapping
    public Goal createGoal(@RequestBody Goal goal) {
        return goalService.createGoal(goal);
    }

    @PutMapping("/{id}")
    public Goal updateGoal(@PathVariable Integer id, @RequestBody Goal goal) {
        return goalService.updateGoal(id, goal);
    }

    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable Integer id) {
        goalService.deleteGoal(id);
    }
}
