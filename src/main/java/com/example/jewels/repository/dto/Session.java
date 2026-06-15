package com.example.jewels.repository.dto;

import java.time.Instant;

public record Session(String token, long userId, Instant expiresAt) {
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
