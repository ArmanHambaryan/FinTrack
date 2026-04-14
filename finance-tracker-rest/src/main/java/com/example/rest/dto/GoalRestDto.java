package com.example.rest.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GoalRestDto(
        Integer id,
        Integer userId,
        String name,
        String currencyCode,
        Double originalTargetAmount,
        Double exchangeRate,
        double targetAmount,
        double savedAmount,
        double progressPercent,
        LocalDate deadline,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
