package com.example.rest.controller;

import com.example.rest.dto.RecurringTransactionRestDto;
import com.example.rest.service.RestDtoMapperService;
import model.RecurringTransaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.RecurringTransactionService;

@RestController
@RequestMapping("/api/recurring-view")
public class RecurringViewRestController {

    private final RecurringTransactionService recurringTransactionService;
    private final RestDtoMapperService mapperService;

    public RecurringViewRestController(RecurringTransactionService recurringTransactionService,
                                       RestDtoMapperService mapperService) {
        this.recurringTransactionService = recurringTransactionService;
        this.mapperService = mapperService;
    }

    @PostMapping("/add")
    public ResponseEntity<RecurringTransactionRestDto> add(@RequestBody RecurringTransaction recurringTransaction) {
        recurringTransaction.setNextRunDate(recurringTransaction.getStartDate());
        recurringTransaction.setActive(true);
        RecurringTransaction saved = recurringTransactionService.save(recurringTransaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperService.toRecurringDto(saved));
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        recurringTransactionService.deactivate(id);
        return "Recurring transaction deleted";
    }
}
