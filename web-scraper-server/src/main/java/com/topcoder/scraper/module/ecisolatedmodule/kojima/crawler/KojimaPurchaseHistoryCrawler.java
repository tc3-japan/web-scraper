package com.topcoder.scraper.module.ecisolatedmodule.kojima.crawler;

import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractPurchaseHistoryCrawler;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Kojima implementation of PurchaseHistoryListCrawler
 */
@Component
public class KojimaPurchaseHistoryCrawler extends AbstractPurchaseHistoryCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaPurchaseHistoryCrawler.class);

  @Autowired
  public KojimaPurchaseHistoryCrawler(WebpageService webpageService) {
    super("kojima", webpageService);
  }

  @Override
  public String getScriptSupportClassName() {
    return KojimaPurchaseHistoryCrawlerScriptSupport.class.getName();
  }
}
