package com.example.rest.controller;

import com.example.rest.dto.GoalRestDto;
import com.example.rest.dto.RecurringTransactionRestDto;
import com.example.rest.dto.UserRestDto;
import model.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.rest.service.BudgetService;
import com.example.rest.service.GoalService;
import com.example.rest.service.RecurringTransactionService;
import com.example.rest.service.TransactionService;
import com.example.rest.service.UserService;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final GoalService goalService;
    private final BudgetService budgetService;
    private final TransactionService transactionService;
    private final RecurringTransactionService recurringTransactionService;

    public UserRestController(UserService userService,
                              GoalService goalService,
                              BudgetService budgetService,
                              TransactionService transactionService,
                              RecurringTransactionService recurringTransactionService) {
        this.userService = userService;
        this.goalService = goalService;
        this.budgetService = budgetService;
        this.transactionService = transactionService;
        this.recurringTransactionService = recurringTransactionService;
    }

    @GetMapping
    public List<UserRestDto> getAllUsers() {
        return userService.findAll().stream().map(user -> new UserRestDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBalance(),
                user.isBlocked(),
                user.getCreated_at(),
                user.getUpdated_at(),
                user.getLastActive())).toList();
    }

    @GetMapping("/{id}")
    public UserRestDto getUser(@PathVariable Integer id) {
        User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserRestDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBalance(),
                user.isBlocked(),
                user.getCreated_at(),
                user.getUpdated_at(),
                user.getLastActive());
    }

    @GetMapping("/{id}/dashboard")
    public LinkedHashMap<String, Object> getDashboard(@PathVariable Integer id) {
        User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        Double budget = budgetService.getCurrentMonthBudget(id);
        Double expense = transactionService.getMonthlyExpense(id);
        if (expense == null) {
            expense = 0.0;
        }

        double percent = 0.0;
        String status = "NO_BUDGET";
        if (budget != null && budget > 0) {
            percent = (expense / budget) * 100;
            status = "OK";
            if (percent >= 100) {
                status = "ALERT";
            } else if (percent >= 80) {
                status = "WARNING";
            }
        }

        List<GoalRestDto> goals = goalService.findByUserId(id).stream().map(goal -> new GoalRestDto(
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
        List<RecurringTransactionRestDto> recurring = recurringTransactionService.getByUser(id).stream()
                .map(item -> new RecurringTransactionRestDto(
                        item.getId(),
                        item.getUserId(),
                        item.getAmount(),
                        item.getCurrency_code(),
                        item.getExchange_rate(),
                        item.getType(),
                        item.getCategoryId(),
                        item.getDescription(),
                        item.getFrequency(),
                        item.getStartDate(),
                        item.getNextRunDate(),
                        item.isActive(),
                        item.getCreated_at()))
                .toList();

        LocalDate now = LocalDate.now();
        String periodLabel = now.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + now.getYear();

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("user", new UserRestDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBalance(),
                user.isBlocked(),
                user.getCreated_at(),
                user.getUpdated_at(),
                user.getLastActive()));
        response.put("currentBalance", user.getBalance());
        response.put("budget", budget == null ? 0.0 : budget);
        response.put("expense", expense);
        response.put("percent", percent);
        response.put("status", status);
        response.put("periodLabel", periodLabel);
        response.put("goals", goals);
        response.put("recurringTransactions", recurring);
        return response;
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userService.deleteById(id);
        return "User deleted";
    }
}
