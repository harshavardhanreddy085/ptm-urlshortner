package com.ptmharsha.urlshortener.util;

import java.net.URI;

public final class UrlValidator {

    private UrlValidator() {
    }

    public static boolean isValid(String url) {

        try {

            URI uri = new URI(url);

            return ("http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null;

        } catch (Exception e) {

            return false;

        }

    }

}
