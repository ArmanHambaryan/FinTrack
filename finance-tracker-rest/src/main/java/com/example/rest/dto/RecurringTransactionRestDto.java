package com.example.rest.dto;

import model.FrequencyType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RecurringTransactionRestDto(
        Integer id,
        Integer userId,
        Double amount,
        String currencyCode,
        Double exchangeRate,
        String type,
        Integer categoryId,
        String description,
        FrequencyType frequency,
        LocalDate startDate,
        LocalDate nextRunDate,
        boolean active,
        LocalDateTime createdAt
) {
}
