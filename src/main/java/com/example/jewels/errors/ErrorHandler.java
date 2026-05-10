package com.example.jewels.errors;

import io.github.wallawood.GeminiResponse;
import io.github.wallawood.annotations.GeminiExceptionHandler;

@GeminiExceptionHandler
public class ErrorHandler {

    public GeminiResponse handle(NotFoundException e) {
        return GeminiResponse.notFound(e.getMessage());
    }

    public GeminiResponse handle(Exception e) {
        return GeminiResponse.temporaryFailure("Something went wrong: " + e.getMessage());
    }
}
