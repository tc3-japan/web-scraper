package com.topcoder.api.exception;

/**
 * This is exception is thrown if there is a data conflict.
 */
public class ConflictException extends ApiException {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 2197604625189278759L;

  /**
   * Create a new instance with message argument.
   *
   * @param message the message
   */
  public ConflictException(String message) {
    super(message);
  }

  /**
   * Create a new instance with message and cause arguments.
   *
   * @param message the message
   * @param cause   the cause of the exception
   */
  public ConflictException(String message, Throwable cause) {
    super(message, cause);
  }
}
