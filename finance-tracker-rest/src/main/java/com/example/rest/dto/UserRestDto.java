package com.example.rest.dto;

import model.UserRole;

import java.time.LocalDateTime;

public record UserRestDto(
        Integer id,
        String username,
        String email,
        UserRole role,
        double balance,
        boolean blocked,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastActive
) {
}
