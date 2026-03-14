package service.impl;

import lombok.RequiredArgsConstructor;
import model.Goal;
import org.springframework.stereotype.Service;
import repository.GoalRepository;
import service.GoalService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;


    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Goal getGoalById(Integer id) {
        return goalRepository.findById(id).orElse(null);
    }

    public Goal createGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    public Goal updateGoal(Integer id, Goal goal) {
        Goal existingGoal = goalRepository.findById(id).orElse(null);

        if (existingGoal != null) {
            existingGoal.setName(goal.getName());
            existingGoal.setTarget_amount(goal.getTarget_amount());
            existingGoal.setSaved_amount(goal.getSaved_amount());
            existingGoal.setDeadline(goal.getDeadline());
            existingGoal.setStatus(goal.getStatus());

            return goalRepository.save(existingGoal);
        }

        return null;
    }

    public void deleteGoal(Integer id) {
        goalRepository.deleteById(id);
    }

    public List<Goal> findByUserId(Integer userId) {
        return goalRepository.findByUserId(userId);
    }
}
