package com.ptmharsha.urlshortener.exception;

public class InvalidURLException extends RuntimeException {

    public InvalidURLException(String message) {
        super(message);
    }

}