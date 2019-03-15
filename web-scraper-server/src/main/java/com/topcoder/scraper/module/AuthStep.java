package com.topcoder.scraper.module;

public enum AuthStep {

  // the first Captcha
  FIRST,

  // the second Captcha
  SECOND,

  // the last Verification or MFA
  LAST,

  // END
  DONE,
}
