package com.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import service.CurrencyRateService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class GoalCalculatorController {

    private final CurrencyRateService currencyRateService;

    @GetMapping("/calculator")
    public String showForm() {
        return "redirect:/user/home";
    }

    @PostMapping("/calculator")
    public String calculate(
            @RequestParam("target_amount") BigDecimal targetAmount,
            @RequestParam int months,
            @RequestParam(name = "currency_code", defaultValue = "AMD") String currencyCode) {

        String normalizedCurrency = normalizeCurrency(currencyCode);
        BigDecimal rateToAmd = currencyRateService.getRateToAmd(normalizedCurrency, LocalDate.now());
        BigDecimal monthlySaving = targetAmount
                .divide(BigDecimal.valueOf(months), 2, RoundingMode.CEILING);
        BigDecimal monthlySavingAmd = monthlySaving
                .multiply(rateToAmd)
                .setScale(2, RoundingMode.HALF_UP);

        return "redirect:" + UriComponentsBuilder.fromPath("/user/home")
                .queryParam("calculatorTargetAmount", targetAmount)
                .queryParam("calculatorMonths", months)
                .queryParam("calculatorCurrencyCode", normalizedCurrency)
                .queryParam("calculatorResult", monthlySaving)
                .queryParam("calculatorResultAmd", monthlySavingAmd)
                .build()
                .toUriString();
    }

    private String normalizeCurrency(String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return "AMD";
        }
        return currencyCode.trim().toUpperCase(Locale.ROOT);
    }
}
