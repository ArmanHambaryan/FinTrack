package com.example.rest.controller;

import com.example.rest.dto.GoalRestDto;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class GoalRestController {

    private final GoalService goalService;


    @GetMapping
    public List<GoalRestDto> getAllGoals() {
        return goalService.getAllGoalDtos();
    }

    @GetMapping("/user/{userId}")
    public List<GoalRestDto> getUserGoals(@PathVariable Integer userId) {
        return goalService.findGoalDtosByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<GoalRestDto> createGoal(@RequestBody Goal goal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goalService.createGoalDto(goal));
    }

    @PostMapping("/{id}/progress")
    public GoalRestDto updateProgress(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Double amount = ((Number) body.get("amount")).doubleValue();
        return goalService.updateProgressAndReturnDto(id, amount);
    }

    @DeleteMapping("/{id}")
    public String deleteGoal(@PathVariable Integer id) {
        goalService.deleteGoal(id);
        return "Goal deleted";
    }
}
