package com.example.rest.dto;

public record BudgetRestDto(
        Integer id,
        Integer userId,
        Double amount,
        int month,
        int year
) {
}
