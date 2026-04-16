package com.example.rest.service.impl;

import model.Budget;
import model.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import repository.BudgetRepository;
import repository.UserRepository;
import com.example.rest.service.BudgetService;

import java.time.LocalDate;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "budgets")
public class BudgetServiceImpl implements BudgetService {

        private final BudgetRepository budgetRepository;
        private final UserRepository userRepository;

    public BudgetServiceImpl(BudgetRepository budgetRepository,
                                 UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    @Override
    @CacheEvict(allEntries = true)
    public void setBudget(Integer userId, Double amount) {

            LocalDate now = LocalDate.now();

            int month = now.getMonthValue();
            int year = now.getYear();

            Optional<Budget> existing =
                    budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);

            if (existing.isPresent()) {
                Budget budget = existing.get();
                budget.setAmount(amount);
                budgetRepository.save(budget);
            } else {
                User user = userRepository.findById(userId).orElseThrow();

                Budget budget = new Budget();
                budget.setAmount(amount);
                budget.setMonth(month);
                budget.setYear(year);
                budget.setUser(user);

                budgetRepository.save(budget);
            }
    }

    @Override
    @Cacheable(key = "#userId")
    public Double getCurrentMonthBudget(Integer userId) {

            LocalDate now = LocalDate.now();

            return budgetRepository
                    .findByUserIdAndMonthAndYear(userId, now.getMonthValue(), now.getYear())
                    .map(Budget::getAmount)
                    .orElse(0.0);
        }
    }
