package com.example.rest.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        UserRestDto user
) {
}
