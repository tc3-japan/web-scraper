package com.topcoder.common.util.scraping;

import com.gargoylesoftware.htmlunit.html.DomNode;

import java.util.List;

public class ScrapingFieldProcessor_DomList extends ScrapingFieldProcessor<List<DomNode>> {
  public ScrapingFieldProcessor_DomList(DomNode domNode, ScrapingFieldProperty scrapingFieldProperty) {
    super(domNode, scrapingFieldProperty);
  }

  @Override
  public List<DomNode> process() {
    return this.domNode.querySelectorAll(this.scrapingFieldProperty.selector);
  }
}
