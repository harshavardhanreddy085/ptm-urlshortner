package com.ptmharsha.urlshortener.util;

public final class Base62Encoder {

    private static final String CHARACTERS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private Base62Encoder() {
    }

    public static String encode(long value) {

        StringBuilder builder = new StringBuilder();

        while (value > 0) {

            builder.append(
                    CHARACTERS.charAt((int) (value % 62))
            );

            value /= 62;

        }

        return builder.reverse().toString();

    }

}