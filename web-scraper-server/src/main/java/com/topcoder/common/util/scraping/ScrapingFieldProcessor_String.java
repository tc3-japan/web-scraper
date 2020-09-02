package com.topcoder.common.util.scraping;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.topcoder.common.util.HtmlUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ScrapingFieldProcessor_String extends ScrapingFieldProcessor<String> {

  private Pattern regexPattern = null;

  public ScrapingFieldProcessor_String(DomNode domNode, ScrapingFieldProperty scrapingFieldProperty) {
    super(domNode, scrapingFieldProperty);
  }

  @Override
  public String process() {
    String value = this.domNode.querySelector(this.scrapingFieldProperty.selector).getTextContent();

    if (StringUtils.isNotEmpty(scrapingFieldProperty.extractRegex)) {
      if (regexPattern == null) {
        this.regexPattern = Pattern.compile(scrapingFieldProperty.extractRegex);
      }
      value = HtmlUtils.extract(value, this.regexPattern);
    }

    return value;
  }
}
