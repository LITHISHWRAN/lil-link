package com.example.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class CreateUrlRequest {

    @NotBlank
    private String originalUrl;

    private LocalDateTime expiresAt;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}