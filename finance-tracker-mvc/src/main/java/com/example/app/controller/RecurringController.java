package com.example.app.controller;

import jakarta.servlet.http.HttpSession;
import model.RecurringTransaction;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RecurringController {

    @Autowired
    private RecurringTransactionService service;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody RecurringTransaction rec,
                                 HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        rec.setUserId(userId);
        rec.setNextRunDate(rec.getStartDate());
        rec.setActive(true);
        return ResponseEntity.ok(service.save(rec));
    }

    @GetMapping("/list")
    public List<RecurringTransaction> list(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        return service.getByUser(userId);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        service.deactivate(id);
        return ResponseEntity.ok("Deactivated");
    }
}
