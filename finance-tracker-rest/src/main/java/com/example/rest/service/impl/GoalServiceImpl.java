package com.example.rest.service.impl;

import lombok.RequiredArgsConstructor;
import model.Goal;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import repository.GoalRepository;
import com.example.rest.service.CurrencyRateService;
import com.example.rest.service.GoalService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "goals")
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final CurrencyRateService currencyRateService;


    @Override
    @Cacheable(key = "'all'")
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    @Override
    @Cacheable(key = "'id:' + #id")
    public Goal getGoalById(Integer id) {
        return goalRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(allEntries = true)
    public Goal createGoal(Goal goal) {
        enrichGoalCurrency(goal);
        return goalRepository.save(goal);
    }

    @Override
    @CacheEvict(allEntries = true)
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

    @Override
    @CacheEvict(allEntries = true)
    public void deleteGoal(Integer id) {
        goalRepository.deleteById(id);
    }

    @Override
    @Cacheable(key = "'user:' + #userId")
    public List<Goal> findByUserId(Integer userId) {
        return goalRepository.findByUserId(userId);
    }

    @Override
    public int calculateProgress(Goal goal) {
        return (int) (goal.getSaved_amount() / goal.getTarget_amount());
    }

    @Override
    @Cacheable(key = "'active:' + #userId")
    public List<Goal> activeGoals(Integer userId) {
        return goalRepository.findActiveGoals(userId);
    }

    @Override
    @Cacheable(key = "'completed:' + #userId")
    public List<Goal> completedGoals(Integer userId) {
        return goalRepository.findByUserIdAndStatus(userId,"COMPLETED");
    }

    @Override
    @CacheEvict(allEntries = true)
    public void updateProgress(Integer Id, double amount) {
        Goal goal = goalRepository.findById(Id).orElseThrow(()->new RuntimeException("Goal not found"));
        if (amount <= 0) {
            return;
        }

        BigDecimal rate = currencyRateService.getRateToAmd(goal.getCurrency_code(), LocalDate.now());
        double amountInAmd = BigDecimal.valueOf(amount)
                .multiply(rate)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        double remainingAmount = goal.getTarget_amount() - goal.getSaved_amount();
        if (amountInAmd > remainingAmount) {
            throw new IllegalArgumentException("Amount exceeds the remaining goal balance.");
        }

        goal.setSaved_amount(goal.getSaved_amount() + amountInAmd);

        if (goal.getSaved_amount() >= goal.getTarget_amount()) {
            goal.setStatus("COMPLETED");
        }
        goalRepository.save(goal);
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
