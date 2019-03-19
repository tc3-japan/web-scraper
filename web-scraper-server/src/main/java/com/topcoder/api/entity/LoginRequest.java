package com.topcoder.api.entity;

import lombok.Data;
import lombok.ToString;

/**
 * the login request class
 */
@Data
@ToString(exclude = {"password"})
public class LoginRequest {

  /**
   * the email
   */
  String email;

  /**
   * the password
   */
  String password;

  /**
   * the site id
   */
  Integer siteId;

  /**
   * the code
   */
  String code;

  /**
   * the session/task id
   */
  String uuid;
}
