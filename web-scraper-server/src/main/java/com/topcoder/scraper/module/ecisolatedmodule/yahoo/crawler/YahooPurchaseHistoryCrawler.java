package com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler;

import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractPurchaseHistoryCrawler;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Yahoo implementation of PurchaseHistoryCrawler
 */
@Component
public class YahooPurchaseHistoryCrawler extends AbstractPurchaseHistoryCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooPurchaseHistoryCrawler.class);

  @Autowired
  public YahooPurchaseHistoryCrawler(WebpageService webpageService) {
    super("yahoo", webpageService);
  }

  @Override
  public String getScriptSupportClassName() {
    return YahooPurchaseHistoryCrawlerScriptSupport.class.getName();
  }

}
