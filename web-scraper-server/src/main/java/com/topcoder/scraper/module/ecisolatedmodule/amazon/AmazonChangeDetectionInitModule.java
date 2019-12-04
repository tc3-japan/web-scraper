package com.topcoder.scraper.module.ecisolatedmodule.amazon;

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
 * Amazon implementation of ChangeDetectionInitModule
 */
@Component
public class AmazonChangeDetectionInitModule extends AbstractChangeDetectionInitModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonChangeDetectionInitModule.class);

  @Autowired
  public AmazonChangeDetectionInitModule(
          MonitorTargetDefinitionProperty  monitorTargetDefinitionProperty,
          WebpageService                   webpageService,
          ECSiteAccountRepository          ecSiteAccountRepository,
          NormalDataRepository             normalDataRepository,
          AmazonPurchaseHistoryListModule  purchaseHistoryListModule,
          AmazonProductModule              productModule
  ) {
    super(
            monitorTargetDefinitionProperty,
            webpageService,
            ecSiteAccountRepository,
            normalDataRepository,
            purchaseHistoryListModule,
            productModule
    );
  }

  @Override
  public String getModuleType() {
    return "amazon";
  }
}
