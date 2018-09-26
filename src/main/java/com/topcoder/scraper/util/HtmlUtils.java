package com.topcoder.scraper.util;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class HtmlUtils {

    public static String getTextContent(HtmlElement element) {
        if(element != null) {
            return element.getTextContent().trim();
        }
        return null;
    }

}
