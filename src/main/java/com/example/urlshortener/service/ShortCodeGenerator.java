package com.example.urlshortener.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class ShortCodeGenerator {

    private static final String CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String generate() {

        StringBuilder result = new StringBuilder(7);

        for (int i = 0; i < 7; i++) {
            int index = ThreadLocalRandom.current()
                    .nextInt(CHARACTERS.length());

            result.append(CHARACTERS.charAt(index));
        }

        return result.toString();
    }
}   