package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import model.RecurringTransaction;
import model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import service.RecurringTransactionService;
import service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recurring")
public class RecurringViewController {

    private final RecurringTransactionService recurringService;
    private final UserService userService;

    @PostMapping("/add")
    public String add(RecurringTransaction rec, Authentication authentication) {
        User user = getUser(authentication);
        if (user == null) {
            return "redirect:/loginPage?msg=User not found";
        }
        rec.setUserId(user.getId());
        rec.setNextRunDate(rec.getStartDate());
        rec.setActive(true);
        recurringService.save(rec);
        return "redirect:/transaction";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        recurringService.deactivate(id);
        return "redirect:/transaction";
    }

    private User getUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return userService.findByEmail(authentication.getName()).orElse(null);
    }
}
