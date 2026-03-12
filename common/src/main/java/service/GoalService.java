package service;

import model.Goal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GoalService {

        List<Goal> getAllGoals();

        Goal getGoalById(Integer id);

        Goal createGoal(Goal goal);

        Goal updateGoal(Integer id, Goal goal);

        void deleteGoal(Integer id);

        List<Goal> findByUserId(Integer userId);
    }
