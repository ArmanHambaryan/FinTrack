package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import model.Category;
import model.Transaction;
import model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import repository.TransactionRepository;
import service.CategoryService;
import service.TransactionService;
import service.UserService;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TransactionRepository transactionRepository;

    @GetMapping
    public String transactionPage(Authentication authentication,
                                  @RequestParam(required = false) String msg,
                                  Model model) {
        User user = getUser(authentication);
        if (user == null) {
            return "redirect:/loginPage?msg=User not found";
        }

        List<Transaction> transactions = transactionService.findAllByUserId(user.getId());
        List<Category> categories = categoryService.getAvailableCategories(user.getId());
        Map<Integer, String> categoryNames = categories.stream()
                .filter(category -> category.getId() != null)
                .collect(Collectors.toMap(Category::getId, Category::getName, (left, right) -> left));

        model.addAttribute("transactions", transactions);
        model.addAttribute("categories", categories);
        model.addAttribute("categoryNames", categoryNames);
        model.addAttribute("msg", msg);

        List<Object[]> categoryData = transactionRepository.getExpensesByCategory(user.getId());
        List<String> chartCategories = new ArrayList<>();
        List<Double> chartAmounts = new ArrayList<>();
        for (Object[] row : categoryData) {
            Integer categoryId = row[0] == null ? null : ((Number) row[0]).intValue();
            String label = categoryId == null ? "Other" : categoryNames.getOrDefault(categoryId, "Other");
            chartCategories.add(label);
            chartAmounts.add(((Number) row[1]).doubleValue());
        }
        model.addAttribute("chartCategories", chartCategories);
        model.addAttribute("chartAmounts", chartAmounts);

        List<Object[]> monthlyData = transactionRepository.getMonthlyStats(user.getId());
        List<String> chartMonths = new ArrayList<>();
        List<Double> chartIncome = new ArrayList<>();
        List<Double> chartExpense = new ArrayList<>();
        for (Object[] row : monthlyData) {
            int month = ((Number) row[0]).intValue();
            chartMonths.add(Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            chartIncome.add(((Number) row[1]).doubleValue());
            chartExpense.add(((Number) row[2]).doubleValue());
        }
        model.addAttribute("chartMonths", chartMonths);
        model.addAttribute("chartIncome", chartIncome);
        model.addAttribute("chartExpense", chartExpense);
        return "transactions";
    }

    @PostMapping("/income")
    public String addIncome(Transaction transaction, Authentication authentication) {
        User user = getUser(authentication);
        if (user == null) {
            return "redirect:/loginPage?msg=User not found";
        }
        transaction.setUserId(user.getId());
        transactionService.addIncome(transaction);
        return "redirect:/transaction";
    }

    @PostMapping("/expense")
    public String addExpense(Transaction transaction,
                             @RequestParam(name = "categoryName") String categoryName,
                             Authentication authentication) {
        User user = getUser(authentication);
        if (user == null) {
            return "redirect:/loginPage?msg=User not found";
        }
        if (categoryName == null || categoryName.isBlank()) {
            return "redirect:/transaction?msg=Category is required for expense";
        }
        Category category = categoryService.findOrCreate(user.getId(), categoryName);
        transaction.setUserId(user.getId());
        transaction.setCategoryId(category.getId());
        transactionService.addExpense(transaction);
        return "redirect:/transaction";
    }

    private User getUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return userService.findByEmail(authentication.getName()).orElse(null);
    }

}
