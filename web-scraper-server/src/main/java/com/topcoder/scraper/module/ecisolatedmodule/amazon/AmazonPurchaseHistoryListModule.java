package com.topcoder.scraper.module.ecisolatedmodule.amazon;

import com.topcoder.api.service.login.amazon.AmazonLoginHandler;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.scraper.module.ecisolatedmodule.AbstractPurchaseHistoryListModule;
import com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler.AmazonPurchaseHistoryListCrawler;
import com.topcoder.scraper.service.PurchaseHistoryService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Amazon implementation of PurchaseHistoryListModule
 */
@Component
public class AmazonPurchaseHistoryListModule extends AbstractPurchaseHistoryListModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonPurchaseHistoryListModule.class);

  @Autowired
  public AmazonPurchaseHistoryListModule(
          PurchaseHistoryService purchaseHistoryService,
          ECSiteAccountRepository ecSiteAccountRepository,
          WebpageService webpageService,
          AmazonLoginHandler loginHandler,
          AmazonPurchaseHistoryListCrawler crawler
  ) {
    super(purchaseHistoryService, ecSiteAccountRepository, webpageService, loginHandler, crawler);
  }

  @Override
  public String getModuleType() {
    return "amazon";
  }
}
