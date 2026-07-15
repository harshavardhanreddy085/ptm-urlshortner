package com.ptmharsha.urlshortener.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

    private final ShortCodeGenerator generator =
            new ShortCodeGenerator();

    @Test
    void shouldGenerateSevenCharacterCode() {

        String code = generator.generate();

        assertNotNull(code);

        assertEquals(7, code.length());

    }

    @Test
    void shouldGenerateUniqueCodes() {

        String first = generator.generate();

        String second = generator.generate();

        assertNotEquals(first, second);

    }

}