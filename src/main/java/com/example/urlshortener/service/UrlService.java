package com.example.urlshortener.service;

import com.example.urlshortener.dto.AnalyticsResponse;
import com.example.urlshortener.dto.CreateUrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.model.Url;
import com.example.urlshortener.repository.UrlRepository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UrlService {

    private final UrlRepository repository;
    private final ShortCodeGenerator generator;
    private final RedisTemplate<String, String> redis;
    @Value("${app.base-url}")
    private String baseUrl;

    public UrlService(
            UrlRepository repository,
            ShortCodeGenerator generator,
            // RedisTemplate<String, String> redis
            @Qualifier("redisTemplate") RedisTemplate<String, String> redis
    ) {
        this.repository = repository;
        this.generator = generator;
        this.redis = redis;
    }

    public UrlResponse create(CreateUrlRequest request) {

        if (request.getExpiresAt() != null &&
                request.getExpiresAt().isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Expiration must be in the future"
            );
        }

        String shortCode;

        while (true) {
            shortCode = generator.generate();

            if (repository.findByShortCode(shortCode).isEmpty()) {
                break;
            }
        }

        repository.save(
                shortCode,
                request.getOriginalUrl(),
                request.getExpiresAt()
        );

        return new UrlResponse(
                shortCode,
                // "http://localhost:8080/" + shortCode,
                baseUrl + "/" + shortCode,
                request.getOriginalUrl(),
                request.getExpiresAt()
        );
    }

    public String getOriginalUrl(String shortCode) {

        String cached = redis.opsForValue()
                .get("url:" + shortCode);

        if (cached != null) {
            repository.incrementClicks(shortCode);
            return cached;
        }

        Url url = repository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException("Short URL not found"));

        if (url.getExpiresAt() != null &&
                url.getExpiresAt().isBefore(LocalDateTime.now())) {

            throw new UrlNotFoundException("Short URL has expired");
        }

        Duration ttl = Duration.ofHours(1);

        if (url.getExpiresAt() != null) {
            ttl = Duration.between(
                    LocalDateTime.now(),
                    url.getExpiresAt()
            );

            if (ttl.isNegative() || ttl.isZero()) {
                throw new UrlNotFoundException("Short URL has expired");
            }
        }

        redis.opsForValue().set(
                "url:" + shortCode,
                url.getOriginalUrl(),
                ttl
        );

        repository.incrementClicks(shortCode);

        return url.getOriginalUrl();
    }

    public AnalyticsResponse analytics(String shortCode) {

        Url url = repository.findByShortCode(shortCode)
                .orElseThrow(() ->
                        new UrlNotFoundException("Short URL not found"));

        return new AnalyticsResponse(
                url.getShortCode(),
                url.getOriginalUrl(),
                url.getClickCount()
        );
    }
}