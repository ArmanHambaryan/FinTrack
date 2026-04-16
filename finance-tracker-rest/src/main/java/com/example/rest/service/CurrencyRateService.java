package com.example.rest.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CurrencyRateService {
    BigDecimal getRateToAmd(String currencyCode, LocalDate rateDate);
}
