package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import model.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.TransactionService;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public List<Transaction> findAll() {
        return transactionService.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Transaction> findByUserId(@PathVariable Integer userId) {
        return transactionService.findAllByUserId(userId);
    }

    @GetMapping("/type/{type}")
    public List<Transaction> findByType(@PathVariable String type) {
        return transactionService.findByType(type);
    }

    @PostMapping
    public Transaction save(@RequestBody Transaction transaction) {
        return transactionService.save(transaction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        transactionService.deleteById(id);
    }
}
