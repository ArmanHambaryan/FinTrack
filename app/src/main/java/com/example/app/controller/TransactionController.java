package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import model.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import service.TransactionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TransactionController {

    @GetMapping("/save")
    public String save(Transaction transaction) {
        return "redirect:/userHome";

    }
    @GetMapping("/delete")
    public String delete(Transaction transaction) {
        return "redirect:/userHome";
    }


}
