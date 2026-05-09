package com.example.jewels.repository.dto;

import java.time.Instant;

public record Jewel(long id, long authorId, String authorName, String body, Instant createdAt) {
}
