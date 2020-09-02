package com.topcoder.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;


/**
 * EC Cookie entity
 */
@Data
@AllArgsConstructor
public class ECCookie {

    public ECCookie() {
    }

    /**
     * the cookie name
     */
    private String name;

    /**
     * the cookie value
     */
    private String value;

    /**
     * the cookie domain
     */
    private String domain;


    /**
     * below properties are from Java Cookie
     */

    private Date expires;
    // saved to db as epoch milliseconds (1970/01/01 0:0:0.000 = 0)
    private String path;
    private boolean secure;
    private boolean httpOnly;

}
