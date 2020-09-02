package com.topcoder.scraper.module.ecisolatedmodule.yahoo;

import com.topcoder.api.service.login.yahoo.YahooLoginHandler;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.scraper.module.ecisolatedmodule.AbstractPurchaseHistoryModule;
import com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler.YahooPurchaseHistoryCrawler;
import com.topcoder.scraper.service.PurchaseHistoryService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Yahoo implementation of PurchaseHistoryModule
 */
@Component
public class YahooPurchaseHistoryModule extends AbstractPurchaseHistoryModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(YahooPurchaseHistoryModule.class);

    @Autowired
    public YahooPurchaseHistoryModule(
            PurchaseHistoryService purchaseHistoryService,
            ECSiteAccountRepository ecSiteAccountRepository,
            WebpageService webpageService,
            YahooLoginHandler loginHandler,
            YahooPurchaseHistoryCrawler crawler
    ) {
        super(purchaseHistoryService, ecSiteAccountRepository, webpageService, loginHandler, crawler);
    }

    @Override
    public String getModuleType() {
        return "yahoo";
    }
}
