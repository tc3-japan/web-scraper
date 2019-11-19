package com.topcoder.scraper.module.ecisolatedmodule.yahoo;

import com.topcoder.api.service.login.yahoo.YahooLoginHandler;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.scraper.module.ecisolatedmodule.AbstractPurchaseHistoryListModule;
import com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler.YahooPurchaseHistoryListCrawler;
import com.topcoder.scraper.service.PurchaseHistoryService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Yahoo implementation of PurchaseHistoryListModule
 */
@Component
public class YahooPurchaseHistoryListModule extends AbstractPurchaseHistoryListModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooPurchaseHistoryListModule.class);

  @Autowired
  public YahooPurchaseHistoryListModule(
          PurchaseHistoryService          purchaseHistoryService,
          ECSiteAccountRepository         ecSiteAccountRepository,
          WebpageService                  webpageService,
          YahooLoginHandler               loginHandler,
          YahooPurchaseHistoryListCrawler crawler
  ) {
    super(purchaseHistoryService, ecSiteAccountRepository, webpageService, loginHandler, crawler);
  }

  @Override
  public String getModuleType() {
    return "yahoo";
  }
}
