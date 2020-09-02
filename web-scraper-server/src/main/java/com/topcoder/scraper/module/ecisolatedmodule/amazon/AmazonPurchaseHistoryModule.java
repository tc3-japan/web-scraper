package com.topcoder.scraper.module.ecisolatedmodule.amazon;

import com.topcoder.api.service.login.amazon.AmazonLoginHandler;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.scraper.module.ecisolatedmodule.AbstractPurchaseHistoryModule;
import com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler.AmazonPurchaseHistoryCrawler;
import com.topcoder.scraper.service.PurchaseHistoryService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Amazon implementation of PurchaseHistoryModule
 */
@Component
public class AmazonPurchaseHistoryModule extends AbstractPurchaseHistoryModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonPurchaseHistoryModule.class);

  @Autowired
  public AmazonPurchaseHistoryModule(
          PurchaseHistoryService           purchaseHistoryService,
          ECSiteAccountRepository          ecSiteAccountRepository,
          WebpageService                   webpageService,
          AmazonLoginHandler               loginHandler,
          AmazonPurchaseHistoryCrawler     crawler
  ) {
    super(purchaseHistoryService, ecSiteAccountRepository, webpageService, loginHandler, crawler);
  }

  @Override
  public String getModuleType() {
    return "amazon";
  }
}
