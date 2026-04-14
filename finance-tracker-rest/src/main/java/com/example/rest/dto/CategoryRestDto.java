package com.example.rest.dto;

import java.time.LocalDateTime;

public record CategoryRestDto(
        Integer id,
        Integer userId,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
