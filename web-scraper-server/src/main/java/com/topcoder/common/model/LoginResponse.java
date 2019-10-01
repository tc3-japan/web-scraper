package com.topcoder.common.model;

import com.topcoder.scraper.module.ecunifiedmodule.AuthStep;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

  /**
   * the email
   */
  private String emailId;

  /**
   * the code type
   */
  private CodeType codeType;

  /**
   * the image(base 64 format)
   */
  private String image;

  /**
   * what step for auth now
   */
  private AuthStep authStep;

  /**
   * failed reason
   */
  private String reason;
}
