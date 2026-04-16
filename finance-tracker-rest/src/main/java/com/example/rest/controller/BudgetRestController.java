package com.example.rest.controller;

import com.example.rest.dto.BudgetRestDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import repository.BudgetRepository;
import com.example.rest.service.BudgetService;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
public class BudgetRestController {

    private final BudgetService budgetService;
    private final BudgetRepository budgetRepository;

    public BudgetRestController(BudgetService budgetService,
                                BudgetRepository budgetRepository) {
        this.budgetService = budgetService;
        this.budgetRepository = budgetRepository;
    }

    @GetMapping("/{userId}")
    public BudgetRestDto getCurrentBudget(@PathVariable Integer userId) {
        LocalDate now = LocalDate.now();
        return budgetRepository.findByUserIdAndMonthAndYear(userId, now.getMonthValue(), now.getYear())
                .map(budget -> new BudgetRestDto(
                        budget.getId(),
                        budget.getUser().getId(),
                        budget.getAmount(),
                        budget.getMonth(),
                        budget.getYear()))
                .orElseGet(() -> new BudgetRestDto(null, userId, 0.0, now.getMonthValue(), now.getYear()));
    }

    @PostMapping
    public BudgetRestDto setBudget(@RequestBody Map<String, Object> body) {
        Integer userId = ((Number) body.get("userId")).intValue();
        Double amount = ((Number) body.get("amount")).doubleValue();
        budgetService.setBudget(userId, amount);
        return getCurrentBudget(userId);
    }
}
