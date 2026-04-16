package com.example.rest.controller;

import com.example.rest.dto.RecurringTransactionRestDto;
import model.RecurringTransaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.rest.service.RecurringTransactionService;

@RestController
@RequestMapping("/api/recurring-view")
public class RecurringViewRestController {

    private final RecurringTransactionService recurringTransactionService;

    public RecurringViewRestController(RecurringTransactionService recurringTransactionService) {
        this.recurringTransactionService = recurringTransactionService;
    }

    @PostMapping("/add")
    public ResponseEntity<RecurringTransactionRestDto> add(@RequestBody RecurringTransaction recurringTransaction) {
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

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        recurringTransactionService.deactivate(id);
        return "Recurring transaction deleted";
    }
}
