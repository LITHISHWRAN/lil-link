package com.example.urlshortener.repository;

import com.example.urlshortener.model.Url;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UrlRepository {

    private final JdbcTemplate jdbcTemplate;

    public UrlRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final org.springframework.jdbc.core.RowMapper<Url> rowMapper =
            (rs, rowNum) -> new Url(
                    rs.getLong("id"),
                    rs.getString("short_code"),
                    rs.getString("original_url"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("expires_at") != null
                            ? rs.getTimestamp("expires_at").toLocalDateTime()
                            : null,
                    rs.getLong("click_count")
            );

    public void save(
            String shortCode,
            String originalUrl,
            LocalDateTime expiresAt
    ) {
        jdbcTemplate.update(
                """
                INSERT INTO urls
                (short_code, original_url, expires_at)
                VALUES (?, ?, ?)
                """,
                shortCode,
                originalUrl,
                expiresAt != null ? Timestamp.valueOf(expiresAt) : null
        );
    }

    public Optional<Url> findByShortCode(String shortCode) {

        List<Url> result = jdbcTemplate.query(
                """
                SELECT *
                FROM urls
                WHERE short_code = ?
                """,
                rowMapper,
                shortCode
        );

        return result.stream().findFirst();
    }

    public void incrementClicks(String shortCode) {

        jdbcTemplate.update(
                """
                UPDATE urls
                SET click_count = click_count + 1
                WHERE short_code = ?
                """,
                shortCode
        );
    }
}