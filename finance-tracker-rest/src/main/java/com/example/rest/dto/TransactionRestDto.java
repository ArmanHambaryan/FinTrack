package com.example.rest.dto;

import java.time.LocalDateTime;

public record TransactionRestDto(
        Integer id,
        Integer userId,
        Double amount,
        Double originalAmount,
        String currencyCode,
        Double exchangeRate,
        String type,
        Integer categoryId,
        LocalDateTime transactionDate,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
