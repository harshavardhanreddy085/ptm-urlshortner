package com.ptmharsha.urlshortener.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlValidatorTest {

    @Test
    void shouldAcceptValidUrl() {

        assertTrue(
                UrlValidator.isValid("https://google.com")
        );

    }

    @Test
    void shouldRejectInvalidUrl() {

        assertFalse(
                UrlValidator.isValid("abc")
        );

    }

    @Test
    void shouldRejectNonHttpSchemes() {

        assertFalse(
                UrlValidator.isValid("ftp://example.com")
        );

    }

    @Test
    void shouldRejectNullUrl() {

        assertFalse(
                UrlValidator.isValid(null)
        );

    }

}
