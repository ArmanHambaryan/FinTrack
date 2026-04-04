package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import model.Goal;
import model.RecurringTransaction;
import model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import service.BudgetService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.GoalService;
import service.RecurringTransactionService;
import service.TransactionService;
import service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final GoalService goalService;
    private final BudgetService budgetService;
    private final TransactionService transactionService;
    private final RecurringTransactionService recurringService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/user/home")
    public String userHome(Model model,
                           Authentication authentication,
                           @RequestParam(required = false) String goalMessage,
                           @RequestParam(required = false) BigDecimal calculatorTargetAmount,
                           @RequestParam(required = false) Integer calculatorMonths,
                           @RequestParam(required = false) BigDecimal calculatorResult,
                           @RequestParam(required = false, defaultValue = "AMD") String calculatorCurrencyCode,
                           @RequestParam(required = false) BigDecimal calculatorResultAmd) {

        String username = authentication.getName();
        User user = userService.findByEmail(username).orElse(null);

        List<Goal> goals = (user == null) ? List.of() : goalService.findByUserId(user.getId());

        model.addAttribute("goals", goals);
        Integer userId = (user == null) ? null : user.getId();
        if (userId != null) {
            model.addAttribute("userId", userId);
        }

        Double budget = (user == null) ? 0.0 : budgetService.getCurrentMonthBudget(user.getId());
        Double expense = (user == null) ? 0.0 : transactionService.getMonthlyExpense(user.getId());
        if (expense == null) expense = 0.0;

        LocalDate now = LocalDate.now();
        String periodLabel = now.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + now.getYear();

        double percent = 0;
        String status = "NO_BUDGET";
        if (budget != null && budget > 0) {
            percent = (expense / budget) * 100;
            status = "OK";
            if (percent >= 100) status = "ALERT";
            else if (percent >= 80) status = "WARNING";
        }

        model.addAttribute("budget", budget);
        model.addAttribute("expense", expense);
        model.addAttribute("percent", percent);
        model.addAttribute("status", status);
        model.addAttribute("periodLabel", periodLabel);
        model.addAttribute("calculatorTargetAmount", calculatorTargetAmount);
        model.addAttribute("calculatorMonths", calculatorMonths);
        model.addAttribute("calculatorResult", calculatorResult);
        model.addAttribute("calculatorCurrencyCode", calculatorCurrencyCode);
        model.addAttribute("calculatorResultAmd", calculatorResultAmd);
        model.addAttribute("goalMessage", goalMessage);
        model.addAttribute("recurringTransaction", new RecurringTransaction());
        List<RecurringTransaction> recurringList =
                (userId == null) ? List.of() : recurringService.getByUser(userId);
        model.addAttribute("recurringList", recurringList);

        return "userHome";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUserById(@PathVariable int id) {
        userService.deleteById(id);
        return "redirect:/admin/home";
    }
    @GetMapping("/forgotPassword")
    public String passwordForgetPage() {
        return "forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String handlePasswordForget(@RequestParam String email,
                                       @RequestParam String oldPassword,
                                       @RequestParam String newPassword,
                                       ModelMap modelMap) {

        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            modelMap.addAttribute("error", "User not found.");
            return "forgotPassword";
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            modelMap.addAttribute("error", "Old password is incorrect.");
            return "forgotPassword";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

        return "redirect:/loginPage?msg=Password updated successfully";
    }
}
