package com.topcoder.scraper.module.ecunifiedmodule;

public enum AuthStep {

    // the first Captcha
    FIRST,

    // the second Captcha
    SECOND,

    // the last Verification or MFA
    LAST,

    // END
    DONE,

    // Error
    ERROR,
}
