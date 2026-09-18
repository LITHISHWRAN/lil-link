package com.example.urlshortener.dto;

public record AnalyticsResponse(
        String shortCode,
        String originalUrl,
        Long clickCount
) {
}