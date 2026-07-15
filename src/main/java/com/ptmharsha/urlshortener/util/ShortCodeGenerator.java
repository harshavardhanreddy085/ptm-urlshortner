package com.ptmharsha.urlshortener.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final String CHARACTERS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final int LENGTH = 7;

    private final SecureRandom random = new SecureRandom();

    public String generate() {

        StringBuilder code = new StringBuilder();

        for (int i = 0; i < LENGTH; i++) {
            code.append(
                    CHARACTERS.charAt(random.nextInt(CHARACTERS.length()))
            );
        }

        return code.toString();
    }
}