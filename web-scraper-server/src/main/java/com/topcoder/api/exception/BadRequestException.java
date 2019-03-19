package com.topcoder.api.exception;

/**
 * This is exception is thrown if params error
 */
public class BadRequestException extends AppException {

  /**
   * Create a new instance with message argument.
   *
   * @param message the message
   */
  public BadRequestException(String message) {
    super(message);
  }

  /**
   * Create a new instance with message and cause arguments.
   *
   * @param message the message
   * @param cause   the cause of the exception
   */
  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
