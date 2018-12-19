package com.topcoder.scraper.util;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.springframework.data.util.Pair;

import java.util.List;

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

  public static Pair<HtmlElement, String> findFirstElementInSelectors(HtmlPage page, List<String> selectors) {
    HtmlElement element  = null;
    for(String selector : selectors) {
      element = page.querySelector(selector);
      if (element != null) {
        return Pair.of(element, selector);
      }
    }
    return null;
  }

}
