package com.topcoder.api.exception;

/**
 * This is exception is thrown if there is no entity with given criteria.
 */
public class EntityNotFoundException extends AppException {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 6981076108348619981L;

  /**
   * Create a new instance with message argument.
   *
   * @param message the message
   */
  public EntityNotFoundException(String message) {
    super(message);
  }

  /**
   * Create a new instance with message and cause arguments.
   *
   * @param message the message
   * @param cause the cause of the exception
   */
  public EntityNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
