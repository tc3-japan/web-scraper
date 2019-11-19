package com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler;

import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractPurchaseHistoryListCrawler;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Yahoo implementation of PurchaseHistoryListCrawler
 */
@Component
public class YahooPurchaseHistoryListCrawler extends AbstractPurchaseHistoryListCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooPurchaseHistoryListCrawler.class);

  @Autowired
  public YahooPurchaseHistoryListCrawler(WebpageService webpageService) {
    super("yahoo", webpageService);
  }

  @Override
  public String getScriptSupportClassName() {
    return YahooPurchaseHistoryListCrawlerScriptSupport.class.getName();
  }

}
