package com.topcoder.common.util;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {

  public static String getTextContent(HtmlElement element) {
    if (element != null) {
      return element.getTextContent().trim();
    }
    return null;
  }

  public static String getNumberAsStringFrom(HtmlElement element) {
    String text = getTextContent(element);
    if (text == null)
      return null;
    return getNumberAsStringFrom(text);
  }
  
  public static String getNumberAsStringFrom(String text) {
    if (text == null)
      return null;
    String num = extract(text, PAT_NUM);
    if (num == null)
      return null;
    return num.replaceAll(",", "");
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
  
  public static String extract(String text, Pattern pat) {
    if (text == null) {
      return "";
    }
    Matcher m = pat.matcher(text);
    if(m.find()) {
      return m.group(0);
    }
    return "";
  }
  
  private static final Pattern PAT_NUM = Pattern.compile("([\\d,-.]+)", Pattern.DOTALL);

  public static Integer extractInt(String text) {
    String intText = extract(text, PAT_NUM);
    if (intText == null || intText.length() == 0) {
      return null;
    }
    return Integer.valueOf(intText.replaceAll(",", ""));
  }

  // TODO: delete (code for experiment)
  public static String foo(String[] args) {
    String ret = "";
    for(String arg : args) {
      ret += " " + arg;
    }
    return ret;
  }
}
