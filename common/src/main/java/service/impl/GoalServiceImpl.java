package service.impl;

import lombok.RequiredArgsConstructor;
import model.Goal;
import org.springframework.stereotype.Service;
import repository.GoalRepository;
import service.CurrencyRateService;
import service.GoalService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final CurrencyRateService currencyRateService;


    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    public Goal getGoalById(Integer id) {
        return goalRepository.findById(id).orElse(null);
    }

    public Goal createGoal(Goal goal) {
        enrichGoalCurrency(goal);
        return goalRepository.save(goal);
    }

    public Goal updateGoal(Integer id, Goal goal) {
        Goal existingGoal = goalRepository.findById(id).orElse(null);

        if (existingGoal != null) {
            existingGoal.setName(goal.getName());
            existingGoal.setCurrency_code(normalizeCurrency(goal.getCurrency_code()));
            existingGoal.setOriginal_target_amount(goal.getOriginal_target_amount());
            existingGoal.setExchange_rate(goal.getExchange_rate());
            existingGoal.setTarget_amount(goal.getTarget_amount());
            existingGoal.setSaved_amount(goal.getSaved_amount());
            existingGoal.setDeadline(goal.getDeadline());
            existingGoal.setStatus(goal.getStatus());

            enrichGoalCurrency(existingGoal);
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

    @Override
    public int calculateProgress(Goal goal) {
        return (int) (goal.getSaved_amount() / goal.getTarget_amount());
    }

    private void enrichGoalCurrency(Goal goal) {
        String currencyCode = normalizeCurrency(goal.getCurrency_code());
        BigDecimal originalAmount = BigDecimal.valueOf(goal.getTarget_amount());
        if (goal.getOriginal_target_amount() != null && goal.getOriginal_target_amount() > 0) {
            originalAmount = BigDecimal.valueOf(goal.getOriginal_target_amount());
        }

        BigDecimal rate = currencyRateService.getRateToAmd(currencyCode, LocalDate.now());
        BigDecimal convertedAmount = originalAmount.multiply(rate).setScale(2, RoundingMode.HALF_UP);

        goal.setCurrency_code(currencyCode);
        goal.setOriginal_target_amount(originalAmount.doubleValue());
        goal.setExchange_rate(rate.doubleValue());
        goal.setTarget_amount(convertedAmount.doubleValue());
    }

    private String normalizeCurrency(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return "AMD";
        }
        return currencyCode.trim().toUpperCase(Locale.ROOT);
    }
}
