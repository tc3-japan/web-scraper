package com.topcoder.scraper.module.ecisolatedmodule.kojima;

import com.topcoder.common.config.CheckItemsDefinitionProperty;
import com.topcoder.common.config.MonitorTargetDefinitionProperty;
import com.topcoder.common.repository.CheckResultRepository;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.NormalDataRepository;
import com.topcoder.scraper.module.ecisolatedmodule.AbstractChangeDetectionCheckModule;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Kojima implementation of ChangeDetectionCheckModule
 */
@Component
public class KojimaChangeDetectionCheckModule extends AbstractChangeDetectionCheckModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaChangeDetectionCheckModule.class);

  @Autowired
  public KojimaChangeDetectionCheckModule(
          MonitorTargetDefinitionProperty monitorTargetDefinitionProperty,
          WebpageService                  webpageService,
          ECSiteAccountRepository         ecSiteAccountRepository,
          NormalDataRepository            normalDataRepository,
          KojimaPurchaseHistoryModule     purchaseHistoryModule,
          KojimaProductModule             productModule,
          CheckItemsDefinitionProperty    checkItemsDefinitionProperty,
          CheckResultRepository           checkResultRepository

  ) {
    super(
            monitorTargetDefinitionProperty,
            webpageService,
            ecSiteAccountRepository,
            normalDataRepository,
            purchaseHistoryModule,
            productModule,
            checkItemsDefinitionProperty,
            checkResultRepository
    );
  }

  @Override
  public String getModuleType() {
    return "kojima";
  }
}
