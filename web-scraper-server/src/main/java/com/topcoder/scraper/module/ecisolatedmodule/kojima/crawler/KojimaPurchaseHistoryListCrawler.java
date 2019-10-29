package com.topcoder.scraper.module.ecisolatedmodule.kojima.crawler;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ecisolatedmodule.kojima.crawler.KojimaPurchaseHistoryListCrawlerScriptSupport;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractPurchaseHistoryListCrawler;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Kojima implementation of PurchaseHistoryListCrawler
 */
@Component
public class KojimaPurchaseHistoryListCrawler extends AbstractPurchaseHistoryListCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaPurchaseHistoryListCrawler.class);

  // TODO : delete, this is for PoC Code that limits the count to go to next page to 3
  protected int nextPageCount;

  @Autowired
  public KojimaPurchaseHistoryListCrawler(WebpageService webpageService) {
    super("kojima", webpageService);
  }

  @Override
  public String getScriptSupportClassName() {
    return KojimaPurchaseHistoryListCrawlerScriptSupport.class.getName();
  }
}
