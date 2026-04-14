package com.example.rest.controller;

import com.example.rest.dto.RecurringTransactionRestDto;
import com.example.rest.service.RestDtoMapperService;
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
import service.RecurringTransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/recurring")
public class RecurringTransactionRestController {

    private final RecurringTransactionService recurringTransactionService;
    private final RestDtoMapperService mapperService;

    public RecurringTransactionRestController(RecurringTransactionService recurringTransactionService,
                                              RestDtoMapperService mapperService) {
        this.recurringTransactionService = recurringTransactionService;
        this.mapperService = mapperService;
    }

    @PostMapping
    public ResponseEntity<RecurringTransactionRestDto> create(@RequestBody RecurringTransaction recurringTransaction) {
        recurringTransaction.setNextRunDate(recurringTransaction.getStartDate());
        recurringTransaction.setActive(true);
        RecurringTransaction saved = recurringTransactionService.save(recurringTransaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperService.toRecurringDto(saved));
    }

    @GetMapping("/user/{userId}")
    public List<RecurringTransactionRestDto> getUserRecurring(@PathVariable Integer userId) {
        return recurringTransactionService.getByUser(userId).stream().map(mapperService::toRecurringDto).toList();
    }

    @DeleteMapping("/{id}")
    public String deactivate(@PathVariable Integer id) {
        recurringTransactionService.deactivate(id);
        return "Recurring transaction deactivated";
    }
}
