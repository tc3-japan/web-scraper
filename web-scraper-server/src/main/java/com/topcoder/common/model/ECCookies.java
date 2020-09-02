package com.topcoder.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.List;


/**
 * all ec cookies for a webclient
 */
@Data
@AllArgsConstructor
public class ECCookies {
    private final static ObjectMapper OB = new ObjectMapper();

    public ECCookies() {
    }

    /**
     * cookies
     */
    List<ECCookie> cookies;

    /**
     * to json string
     *
     * @return the json string
     */
    public String toJSONString() {
        try {
            return OB.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * parse string from
     *
     * @param value the json string value
     * @return the ECCookies
     */
    public static ECCookies fromJSON(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return OB.readValue(value, ECCookies.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
