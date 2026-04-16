package com.example.rest.controller;

import com.example.rest.dto.GoalRestDto;
import model.Goal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.rest.service.GoalService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class GoalRestController {

    private final GoalService goalService;

    public GoalRestController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public List<GoalRestDto> getAllGoals() {
        return goalService.getAllGoals().stream().map(goal -> new GoalRestDto(
                goal.getId(),
                goal.getUserId(),
                goal.getName(),
                goal.getCurrency_code(),
                goal.getOriginal_target_amount(),
                goal.getExchange_rate(),
                goal.getTarget_amount(),
                goal.getSaved_amount(),
                goal.getProgressPercent(),
                goal.getDeadline(),
                goal.getStatus(),
                goal.getCreated_at(),
                goal.getUpdated_at())).toList();
    }

    @GetMapping("/user/{userId}")
    public List<GoalRestDto> getUserGoals(@PathVariable Integer userId) {
        return goalService.findByUserId(userId).stream().map(goal -> new GoalRestDto(
                goal.getId(),
                goal.getUserId(),
                goal.getName(),
                goal.getCurrency_code(),
                goal.getOriginal_target_amount(),
                goal.getExchange_rate(),
                goal.getTarget_amount(),
                goal.getSaved_amount(),
                goal.getProgressPercent(),
                goal.getDeadline(),
                goal.getStatus(),
                goal.getCreated_at(),
                goal.getUpdated_at())).toList();
    }

    @PostMapping
    public ResponseEntity<GoalRestDto> createGoal(@RequestBody Goal goal) {
        Goal savedGoal = goalService.createGoal(goal);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GoalRestDto(
                savedGoal.getId(),
                savedGoal.getUserId(),
                savedGoal.getName(),
                savedGoal.getCurrency_code(),
                savedGoal.getOriginal_target_amount(),
                savedGoal.getExchange_rate(),
                savedGoal.getTarget_amount(),
                savedGoal.getSaved_amount(),
                savedGoal.getProgressPercent(),
                savedGoal.getDeadline(),
                savedGoal.getStatus(),
                savedGoal.getCreated_at(),
                savedGoal.getUpdated_at()));
    }

    @PostMapping("/{id}/progress")
    public GoalRestDto updateProgress(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Double amount = ((Number) body.get("amount")).doubleValue();
        goalService.updateProgress(id, amount);
        Goal goal = goalService.getGoalById(id);
        return new GoalRestDto(
                goal.getId(),
                goal.getUserId(),
                goal.getName(),
                goal.getCurrency_code(),
                goal.getOriginal_target_amount(),
                goal.getExchange_rate(),
                goal.getTarget_amount(),
                goal.getSaved_amount(),
                goal.getProgressPercent(),
                goal.getDeadline(),
                goal.getStatus(),
                goal.getCreated_at(),
                goal.getUpdated_at());
    }

    @DeleteMapping("/{id}")
    public String deleteGoal(@PathVariable Integer id) {
        goalService.deleteGoal(id);
        return "Goal deleted";
    }
}
