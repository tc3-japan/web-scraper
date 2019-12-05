package com.topcoder.scraper.module.ecisolatedmodule.kojima;

import com.topcoder.api.service.login.kojima.KojimaLoginHandler;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.scraper.module.ecisolatedmodule.AbstractPurchaseHistoryListModule;
import com.topcoder.scraper.module.ecisolatedmodule.kojima.crawler.KojimaPurchaseHistoryCrawler;
import com.topcoder.scraper.service.PurchaseHistoryService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Kojima implementation of PurchaseHistoryListModule
 */
@Component
public class KojimaPurchaseHistoryListModule extends AbstractPurchaseHistoryListModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaPurchaseHistoryListModule.class);

  @Autowired
  public KojimaPurchaseHistoryListModule(
          PurchaseHistoryService           purchaseHistoryService,
          ECSiteAccountRepository          ecSiteAccountRepository,
          WebpageService                   webpageService,
          KojimaLoginHandler               loginHandler,
          KojimaPurchaseHistoryCrawler crawler
  ) {
    super(purchaseHistoryService, ecSiteAccountRepository, webpageService, loginHandler, crawler);
  }

  @Override
  public String getModuleType() {
    return "kojima";
  }
}
