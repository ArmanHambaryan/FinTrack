package com.example.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.rest.service.CurrencyRateService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/calculator")
public class GoalCalculatorRestController {

    private final CurrencyRateService currencyRateService;

    public GoalCalculatorRestController(CurrencyRateService currencyRateService) {
        this.currencyRateService = currencyRateService;
    }

    @GetMapping
    public String showForm() {
        return "Use POST /api/calculator with targetAmount, months, currencyCode";
    }

    @PostMapping
    public LinkedHashMap<String, Object> calculate(@RequestBody Map<String, Object> body) {
        BigDecimal targetAmount = new BigDecimal(String.valueOf(body.get("targetAmount")));
        int months = Integer.parseInt(String.valueOf(body.get("months")));
        String normalizedCurrency = normalizeCurrency((String) body.getOrDefault("currencyCode", "AMD"));

        BigDecimal rateToAmd = currencyRateService.getRateToAmd(normalizedCurrency, LocalDate.now());
        BigDecimal monthlySaving = targetAmount.divide(BigDecimal.valueOf(months), 2, RoundingMode.CEILING);
        BigDecimal monthlySavingAmd = monthlySaving.multiply(rateToAmd).setScale(2, RoundingMode.HALF_UP);

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("targetAmount", targetAmount);
        response.put("months", months);
        response.put("currencyCode", normalizedCurrency);
        response.put("monthlySaving", monthlySaving);
        response.put("monthlySavingAmd", monthlySavingAmd);
        return response;
    }

    private String normalizeCurrency(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return "AMD";
        }
        return currencyCode.trim().toUpperCase(Locale.ROOT);
    }
}
