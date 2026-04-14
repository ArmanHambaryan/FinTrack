package com.example.rest.controller;

import com.example.rest.dto.GoalRestDto;
import com.example.rest.dto.RecurringTransactionRestDto;
import com.example.rest.dto.UserRestDto;
import com.example.rest.service.RestDtoMapperService;
import model.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.BudgetService;
import service.GoalService;
import service.RecurringTransactionService;
import service.TransactionService;
import service.UserService;

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
    private final RestDtoMapperService mapperService;

    public UserRestController(UserService userService,
                              GoalService goalService,
                              BudgetService budgetService,
                              TransactionService transactionService,
                              RecurringTransactionService recurringTransactionService,
                              RestDtoMapperService mapperService) {
        this.userService = userService;
        this.goalService = goalService;
        this.budgetService = budgetService;
        this.transactionService = transactionService;
        this.recurringTransactionService = recurringTransactionService;
        this.mapperService = mapperService;
    }

    @GetMapping
    public List<UserRestDto> getAllUsers() {
        return userService.findAll().stream().map(mapperService::toUserDto).toList();
    }

    @GetMapping("/{id}")
    public UserRestDto getUser(@PathVariable Integer id) {
        User user = userService.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return mapperService.toUserDto(user);
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

        List<GoalRestDto> goals = goalService.findByUserId(id).stream().map(mapperService::toGoalDto).toList();
        List<RecurringTransactionRestDto> recurring = recurringTransactionService.getByUser(id).stream()
                .map(mapperService::toRecurringDto)
                .toList();

        LocalDate now = LocalDate.now();
        String periodLabel = now.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + now.getYear();

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("user", mapperService.toUserDto(user));
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
