package com.topcoder.scraper.module.ecisolatedmodule.kojima;

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
 * Kojima implementation of ChangeDetectionInitModule
 */
@Component
public class KojimaChangeDetectionInitModule extends AbstractChangeDetectionInitModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(KojimaChangeDetectionInitModule.class);

    @Autowired
    public KojimaChangeDetectionInitModule(
            MonitorTargetDefinitionProperty monitorTargetDefinitionProperty,
            WebpageService webpageService,
            ECSiteAccountRepository ecSiteAccountRepository,
            NormalDataRepository normalDataRepository,
            KojimaPurchaseHistoryModule purchaseHistoryModule,
            KojimaProductModule productModule
    ) {
        super(
                monitorTargetDefinitionProperty,
                webpageService,
                ecSiteAccountRepository,
                normalDataRepository,
                purchaseHistoryModule,
                productModule
        );
    }

    @Override
    public String getModuleType() {
        return "kojima";
    }
}
