package com.example.rest.controller;

import com.example.rest.dto.CategoryRestDto;
import com.example.rest.dto.TransactionRestDto;
import com.example.rest.service.RestDtoMapperService;
import model.Category;
import model.Transaction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import repository.TransactionRepository;
import service.CategoryService;
import service.TransactionService;

import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final TransactionRepository transactionRepository;
    private final RestDtoMapperService mapperService;

    public TransactionRestController(TransactionService transactionService,
                                     CategoryService categoryService,
                                     TransactionRepository transactionRepository,
                                     RestDtoMapperService mapperService) {
        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.transactionRepository = transactionRepository;
        this.mapperService = mapperService;
    }

    @GetMapping("/user/{userId}")
    public LinkedHashMap<String, Object> getTransactions(@PathVariable Integer userId) {
        List<Transaction> transactions = transactionService.findAllByUserId(userId);
        List<Category> categories = categoryService.getAvailableCategories(userId);
        Map<Integer, String> categoryNames = categories.stream()
                .filter(category -> category.getId() != null)
                .collect(Collectors.toMap(Category::getId, Category::getName, (left, right) -> left));

        List<String> expenseLabels = new ArrayList<>();
        List<Double> expenseAmounts = new ArrayList<>();
        for (Object[] row : transactionRepository.getExpensesByCategory(userId)) {
            Integer categoryId = row[0] == null ? null : ((Number) row[0]).intValue();
            String label = categoryId == null ? "Other" : categoryNames.getOrDefault(categoryId, "Other");
            expenseLabels.add(label);
            expenseAmounts.add(((Number) row[1]).doubleValue());
        }

        List<String> months = new ArrayList<>();
        List<Double> incomeSeries = new ArrayList<>();
        List<Double> expenseSeries = new ArrayList<>();
        for (Object[] row : transactionRepository.getMonthlyStats(userId)) {
            int month = ((Number) row[0]).intValue();
            months.add(Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            incomeSeries.add(((Number) row[1]).doubleValue());
            expenseSeries.add(((Number) row[2]).doubleValue());
        }

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("transactions", transactions.stream().map(mapperService::toTransactionDto).toList());
        response.put("categories", categories.stream()
                .map(category -> new CategoryRestDto(
                        category.getId(),
                        category.getUserId(),
                        category.getName(),
                        category.getCreated_at(),
                        category.getUpdated_at()))
                .toList());
        response.put("expenseLabels", expenseLabels);
        response.put("expenseAmounts", expenseAmounts);
        response.put("months", months);
        response.put("incomeSeries", incomeSeries);
        response.put("expenseSeries", expenseSeries);
        return response;
    }

    @PostMapping("/income")
    public TransactionRestDto addIncome(@RequestBody Transaction transaction) {
        transactionService.addIncome(transaction);
        return mapperService.toTransactionDto(transaction);
    }

    @PostMapping("/expense")
    public TransactionRestDto addExpense(@RequestBody Map<String, Object> body) {
        Integer userId = ((Number) body.get("userId")).intValue();
        String categoryName = String.valueOf(body.get("categoryName"));

        Category category = categoryService.findOrCreate(userId, categoryName);
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setAmount(body.get("amount") == null ? null : ((Number) body.get("amount")).doubleValue());
        transaction.setOriginal_amount(body.get("originalAmount") == null ? null : ((Number) body.get("originalAmount")).doubleValue());
        transaction.setCurrency_code((String) body.get("currencyCode"));
        transaction.setDescription((String) body.get("description"));
        transaction.setCategoryId(category.getId());
        transactionService.addExpense(transaction);
        return mapperService.toTransactionDto(transaction);
    }

    @DeleteMapping("/{id}")
    public String deleteTransaction(@PathVariable Integer id) {
        transactionService.deleteById(id);
        return "Transaction deleted";
    }

    @GetMapping("/export/{userId}")
    public ResponseEntity<byte[]> exportTransactions(@PathVariable Integer userId) throws IOException {
        byte[] data = transactionService.exportToExcel(userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @GetMapping("/categories/{userId}")
    public List<CategoryRestDto> getCategories(@PathVariable Integer userId) {
        return categoryService.getAvailableCategories(userId).stream()
                .map(category -> new CategoryRestDto(
                        category.getId(),
                        category.getUserId(),
                        category.getName(),
                        category.getCreated_at(),
                        category.getUpdated_at()))
                .toList();
    }
}
