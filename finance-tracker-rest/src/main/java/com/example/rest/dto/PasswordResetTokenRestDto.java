package com.example.rest.dto;

import java.time.LocalDateTime;

public record PasswordResetTokenRestDto(
        int id,
        String token,
        Integer userId,
        String userEmail,
        LocalDateTime expiryDate,
        boolean expired
) {
}
