package com.example.urlshortener.controller;

import com.example.urlshortener.dto.AnalyticsResponse;
import com.example.urlshortener.dto.CreateUrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.service.RateLimitService;
import com.example.urlshortener.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/urls")
public class UrlController {

    private final UrlService urlService;
    private final RateLimitService rateLimitService;

    public UrlController(
            UrlService urlService,
            RateLimitService rateLimitService
    ) {
        this.urlService = urlService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody CreateUrlRequest request,
            HttpServletRequest httpRequest
    ) {

        String ip = httpRequest.getRemoteAddr();

        if (!rateLimitService.isAllowed(ip)) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded");
        }

        return ResponseEntity.ok(
                urlService.create(request)
        );
    }

    @GetMapping("/{shortCode}/analytics")
    public AnalyticsResponse analytics(
            @PathVariable String shortCode
    ) {
        return urlService.analytics(shortCode);
    }
}