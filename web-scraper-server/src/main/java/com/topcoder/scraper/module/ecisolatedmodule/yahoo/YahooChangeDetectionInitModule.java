package com.topcoder.scraper.module.ecisolatedmodule.yahoo;

import com.topcoder.common.config.MonitorTargetDefinitionProperty;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.NormalDataRepository;
import com.topcoder.scraper.module.ecisolatedmodule.AbstractChangeDetectionInitModule;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Yahoo implementation of ChangeDetectionInitModule
 */
@Component
public class YahooChangeDetectionInitModule extends AbstractChangeDetectionInitModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooChangeDetectionInitModule.class);

  @Autowired
  public YahooChangeDetectionInitModule(
          MonitorTargetDefinitionProperty monitorTargetDefinitionProperty,
          WebpageService                  webpageService,
          ECSiteAccountRepository         ecSiteAccountRepository,
          NormalDataRepository            normalDataRepository,
          YahooPurchaseHistoryListModule  purchaseHistoryListModule,
          YahooProductDetailModule        productDetailModule
  ) {
    super(
            monitorTargetDefinitionProperty,
            webpageService,
            ecSiteAccountRepository,
            normalDataRepository,
            purchaseHistoryListModule,
            productDetailModule
    );
  }

  @Override
  public String getModuleType() {
    return "yahoo";
  }
}
