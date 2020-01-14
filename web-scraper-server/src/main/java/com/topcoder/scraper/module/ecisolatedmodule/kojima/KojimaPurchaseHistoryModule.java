package com.topcoder.scraper.module.ecisolatedmodule.kojima;

import com.topcoder.api.service.login.kojima.KojimaLoginHandler;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.scraper.module.ecisolatedmodule.AbstractPurchaseHistoryModule;
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
public class KojimaPurchaseHistoryModule extends AbstractPurchaseHistoryModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaPurchaseHistoryModule.class);

  @Autowired
  public KojimaPurchaseHistoryModule(
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
