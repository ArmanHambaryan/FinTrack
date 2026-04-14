package com.example.rest.controller;

import com.example.rest.dto.GoalRestDto;
import com.example.rest.service.RestDtoMapperService;
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
import service.GoalService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class GoalRestController {

    private final GoalService goalService;
    private final RestDtoMapperService mapperService;

    public GoalRestController(GoalService goalService, RestDtoMapperService mapperService) {
        this.goalService = goalService;
        this.mapperService = mapperService;
    }

    @GetMapping
    public List<GoalRestDto> getAllGoals() {
        return goalService.getAllGoals().stream().map(mapperService::toGoalDto).toList();
    }

    @GetMapping("/user/{userId}")
    public List<GoalRestDto> getUserGoals(@PathVariable Integer userId) {
        return goalService.findByUserId(userId).stream().map(mapperService::toGoalDto).toList();
    }

    @PostMapping
    public ResponseEntity<GoalRestDto> createGoal(@RequestBody Goal goal) {
        Goal savedGoal = goalService.createGoal(goal);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperService.toGoalDto(savedGoal));
    }

    @PostMapping("/{id}/progress")
    public GoalRestDto updateProgress(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Double amount = ((Number) body.get("amount")).doubleValue();
        goalService.updateProgress(id, amount);
        Goal goal = goalService.getGoalById(id);
        return mapperService.toGoalDto(goal);
    }

    @DeleteMapping("/{id}")
    public String deleteGoal(@PathVariable Integer id) {
        goalService.deleteGoal(id);
        return "Goal deleted";
    }
}
