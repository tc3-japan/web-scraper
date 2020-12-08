package com.topcoder.scraper.module.ecunifiedmodule;

import com.topcoder.common.config.MonitorTargetDefinitionProperty;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.NormalDataRepository;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.module.IBasicModule;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductDetailCrawlerResult;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralPurchaseHistoryCrawlerResult;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Common class of GeneralChangeDetectionInitModule and GeneralChangeDetectionCheckModule
 */
public abstract class GeneralChangeDetectionCommonModule implements IBasicModule {

    private static Logger LOGGER = LoggerFactory.getLogger(GeneralChangeDetectionCommonModule.class);

    protected final MonitorTargetDefinitionProperty monitorTargetDefinitionProperty;
    protected final WebpageService webpageService;
    protected final ECSiteAccountRepository ecSiteAccountRepository;
    protected final NormalDataRepository normalDataRepository;
    protected final GeneralPurchaseHistoryModule purchaseHistoryModule;
    protected final GeneralProductDetailModule productModule;

    public GeneralChangeDetectionCommonModule(
            MonitorTargetDefinitionProperty monitorTargetDefinitionProperty,
            WebpageService webpageService,
            ECSiteAccountRepository ecSiteAccountRepository,
            NormalDataRepository normalDataRepository,
            GeneralPurchaseHistoryModule purchaseHistoryModule,
            GeneralProductDetailModule productModule
    ) {
        this.monitorTargetDefinitionProperty = monitorTargetDefinitionProperty;
        this.webpageService = webpageService;
        this.ecSiteAccountRepository = ecSiteAccountRepository;
        this.normalDataRepository = normalDataRepository;
        this.purchaseHistoryModule = purchaseHistoryModule;
        this.productModule = productModule;
    }

    @Override
    public String getModuleType() {
        return "general";
    }

    /**
     * Implementation of check method
     */
    protected void processMonitorTarget(List<String> sites) throws IOException {
        this.processMonitorTarget(sites, Consts.TARGET_ALL);
    }

    protected void processMonitorTarget(List<String> sites, String target) throws IOException {
        LOGGER.debug("[processMonitorTarget] in");
        for (MonitorTargetDefinitionProperty.MonitorTargetCheckSite checkSite : monitorTargetDefinitionProperty.getCheckSites()) {
            if (!sites.contains(checkSite.getEcSite())) {
                continue;
            }

            for (MonitorTargetDefinitionProperty.MonitorTargetCheckPage monitorTargetCheckPage : checkSite.getCheckPages()) {

                if (monitorTargetCheckPage.getPageName().equalsIgnoreCase(Consts.PURCHASE_HISTORY_LIST_PAGE_NAME)) {
                    if (!target.equalsIgnoreCase(Consts.TARGET_ALL) && !target.equalsIgnoreCase(Consts.TARGET_PURCHASE_HISTORY)) {
                        continue;
                    }
                    LOGGER.debug("[processMonitorTarget] processPurchaseHistory for target accounts");

                    List<Integer> userIdList = monitorTargetCheckPage.getCheckTargetKeys()
                            .stream().map(e -> Integer.valueOf(e)).collect(Collectors.toList());
                    LOGGER.debug("[processMonitorTarget] account ids: " + userIdList);
                    Iterable<ECSiteAccountDAO> accountDAOS = ecSiteAccountRepository.findAllByEcSiteAndUserIdIn(checkSite.getEcSite(), userIdList);

                    for (ECSiteAccountDAO ecSiteAccountDAO : accountDAOS) {
                        GeneralPurchaseHistoryCrawlerResult crawlerResult =
                                this.purchaseHistoryModule.fetchPurchaseHistoryListForECSiteAccount(ecSiteAccountDAO, null);
                        if (crawlerResult != null) {
                            String key = Integer.toString(ecSiteAccountDAO.getId());
                            this.processPurchaseHistory(checkSite.getEcSite(), crawlerResult, key);
                        }
                    }

                    // process puchase history
                } else if (monitorTargetCheckPage.getPageName().equalsIgnoreCase(Consts.PRODUCT_DETAIL_PAGE_NAME)) {
                    if (!target.equalsIgnoreCase(Consts.TARGET_ALL) && !target.equalsIgnoreCase(Consts.TARGET_PRODUCT)) {
                        continue;
                    }
                    LOGGER.debug("[processMonitorTarget] processProductDetail for target products");

                    // TODO: fix below hacky code, monitorTargetCheckPage.getCheckTargetKeys() must not be null.
                    if (monitorTargetCheckPage.getCheckTargetKeys() == null) continue;

                    for (String productCode : monitorTargetCheckPage.getCheckTargetKeys()) {
                        GeneralProductDetailCrawlerResult crawlerResult =
                                this.productModule.fetchProductDetail(checkSite.getEcSite(), productCode);
                        if (crawlerResult != null) {
                            this.processProductInfo(checkSite.getEcSite(), crawlerResult);
                        }
                    }

                } else {
                    throw new RuntimeException("[processMonitorTarget] Unknown monitor target definition " + monitorTargetCheckPage.getPageName());
                }
            }
        }
    }

    /**
     * Process purchase history crawler result
     *
     * @param site          ec site
     * @param crawlerResult the crawler result
     * @param pageKey       the page key
     */
    abstract protected void processPurchaseHistory(String site, GeneralPurchaseHistoryCrawlerResult crawlerResult, String pageKey);

    /**
     * Process product info crawler result
     *
     * @param site          ec site
     * @param crawlerResult the crawler result
     */
    abstract protected void processProductInfo(String site, GeneralProductDetailCrawlerResult crawlerResult);
}
