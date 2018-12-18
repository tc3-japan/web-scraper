package com.topcoder.scraper.util;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class HtmlUtils {

  public static String getTextContent(HtmlElement element) {
    if (element != null) {
      return element.getTextContent().trim();
    }
    return null;
  }

  public static String getAnchorHref(HtmlElement element) {
    if (element != null) {
      return ((HtmlAnchor) element).getHrefAttribute();
    }
    return null;
  }

  public static String getTextContentWithoutDuplicatedSpaces(HtmlElement element) {
    if (element != null) {
      return element.getTextContent().trim().replaceAll("\\s+", " ");
    }
    return null;
  }
}
