package com.topcoder.scraper.exception;

public class SessionExpiredException extends RuntimeException {

  public SessionExpiredException() {
  }

  public SessionExpiredException(String message) {
    super(message);
  }

  public SessionExpiredException(Throwable cause) {
    super(cause);
  }

  public SessionExpiredException(String message, Throwable cause) {
    super(message, cause);
  }

}
