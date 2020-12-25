package com.topcoder.scraper.exception;

public class NotLoggedinException extends RuntimeException {
    public NotLoggedinException() {
    }

    public NotLoggedinException(String message) { super(message); }
}
