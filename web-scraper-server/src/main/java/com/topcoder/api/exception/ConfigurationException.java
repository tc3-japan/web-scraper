package com.topcoder.api.exception;

/**
 * This is the base exception of the configuration exception.
 */
public class ConfigurationException extends RuntimeException {

  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = -5073963263497851791L;

  /**
   * Create a new instance with message argument.
   *
   * @param message the message
   */
  public ConfigurationException(String message) {
    super(message);
  }

  /**
   * Create a new instance with message and cause arguments.
   *
   * @param message the message
   * @param cause the cause of the exception
   */
  public ConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }
}
