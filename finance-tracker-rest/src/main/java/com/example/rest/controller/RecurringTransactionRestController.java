package com.example.rest.controller;

import com.example.rest.dto.RecurringTransactionRestDto;
import model.RecurringTransaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.rest.service.RecurringTransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/recurring")
public class RecurringTransactionRestController {

    private final RecurringTransactionService recurringTransactionService;

    public RecurringTransactionRestController(RecurringTransactionService recurringTransactionService) {
        this.recurringTransactionService = recurringTransactionService;
    }

    @PostMapping
    public ResponseEntity<RecurringTransactionRestDto> create(@RequestBody RecurringTransaction recurringTransaction) {
        recurringTransaction.setNextRunDate(recurringTransaction.getStartDate());
        recurringTransaction.setActive(true);
        RecurringTransaction saved = recurringTransactionService.save(recurringTransaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RecurringTransactionRestDto(
                saved.getId(),
                saved.getUserId(),
                saved.getAmount(),
                saved.getCurrency_code(),
                saved.getExchange_rate(),
                saved.getType(),
                saved.getCategoryId(),
                saved.getDescription(),
                saved.getFrequency(),
                saved.getStartDate(),
                saved.getNextRunDate(),
                saved.isActive(),
                saved.getCreated_at()));
    }

    @GetMapping("/user/{userId}")
    public List<RecurringTransactionRestDto> getUserRecurring(@PathVariable Integer userId) {
        return recurringTransactionService.getByUser(userId).stream().map(saved -> new RecurringTransactionRestDto(
                saved.getId(),
                saved.getUserId(),
                saved.getAmount(),
                saved.getCurrency_code(),
                saved.getExchange_rate(),
                saved.getType(),
                saved.getCategoryId(),
                saved.getDescription(),
                saved.getFrequency(),
                saved.getStartDate(),
                saved.getNextRunDate(),
                saved.isActive(),
                saved.getCreated_at())).toList();
    }

    @DeleteMapping("/{id}")
    public String deactivate(@PathVariable Integer id) {
        recurringTransactionService.deactivate(id);
        return "Recurring transaction deactivated";
    }
}
