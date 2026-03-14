package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import model.Transaction;
import model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.TransactionService;
import service.UserService;

import java.util.List;

@Controller
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping("/all")
    @ResponseBody
    public List<Transaction> findAll() {
        return transactionService.findAll();
    }

    @GetMapping("/user/{userId}")
    @ResponseBody
    public List<Transaction> findByUserId(@PathVariable Integer userId) {
        return transactionService.findAllByUserId(userId);
    }

    @GetMapping("/type/{type}")
    @ResponseBody
    public List<Transaction> findByType(@PathVariable String type) {
        return transactionService.findByType(type);
    }

    @PostMapping("/save")
    @ResponseBody
    public Transaction save(@RequestBody Transaction transaction) {
        return transactionService.save(transaction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        transactionService.deleteById(id);
    }

    @PostMapping("/income")
    public String addIncome(@ModelAttribute Transaction transaction,
                            Authentication authentication) {

        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        transaction.setUserId(user.getId());

        transactionService.addIncome(transaction);

        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        if (role.equals("ADMIN")) {
            return "redirect:/admin/home";
        } else {
            return "redirect:/user/home";
        }
    }

    @PostMapping("/expense")
    public String addExpense(@ModelAttribute Transaction transaction,
                             Authentication authentication) {

        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        transaction.setUserId(user.getId());

        transactionService.addExpense(transaction);

        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        if (role.equals("ADMIN")) {
            return "redirect:/admin/home";
        } else {
            return "redirect:/user/home";
        }
    }
    @GetMapping
    public String transactionPage(Authentication authentication, Model model) {
        User user = userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        List<Transaction> transactions = transactionService.findAllByUserId(user.getId());
        model.addAttribute("transactions", transactions);
        return "transactions";
    }
}
