package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.BudgetService;

@Controller
    @RequestMapping("/budget")
    public class BudgetController {

        private final BudgetService budgetService;

        public BudgetController(BudgetService budgetService) {
            this.budgetService = budgetService;
        }

    @PostMapping("/set")
    public String setBudget(@RequestParam Double amount,
                            @RequestParam Integer userId) {

        budgetService.setBudget(userId, amount);

        return "redirect:/user/home";
    }
}

