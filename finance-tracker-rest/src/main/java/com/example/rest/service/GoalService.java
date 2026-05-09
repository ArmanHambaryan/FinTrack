package com.example.rest.service;

import com.example.rest.dto.GoalRestDto;
import model.Goal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoalService {

        List<Goal> getAllGoals();

        List<GoalRestDto> getAllGoalDtos();

        Goal getGoalById(Integer id);

        Goal createGoal(Goal goal);

        GoalRestDto createGoalDto(Goal goal);

        Goal updateGoal(Integer id, Goal goal);

        void deleteGoal(Integer id);

        List<Goal> findByUserId(Integer userId);

        List<GoalRestDto> findGoalDtosByUserId(Integer userId);

        int calculateProgress(Goal goal);

        List<Goal> activeGoals(Integer userId);

        List<Goal> completedGoals(Integer userId);

        void updateProgress(Integer Id, double amount);

        GoalRestDto updateProgressAndReturnDto(Integer id, double amount);



    }
