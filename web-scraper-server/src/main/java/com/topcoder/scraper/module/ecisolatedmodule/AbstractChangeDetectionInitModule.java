package com.topcoder.scraper.module.ecisolatedmodule;

import com.topcoder.common.config.MonitorTargetDefinitionProperty;
import com.topcoder.common.dao.NormalDataDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.NormalDataRepository;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.module.IChangeDetectionInitModule;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractProductCrawlerResult;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractPurchaseHistoryCrawlerResult;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Abstract class of ChangeDetectionInitModule
 */
public abstract class AbstractChangeDetectionInitModule extends AbstractChangeDetectionCommonModule implements IChangeDetectionInitModule {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractChangeDetectionInitModule.class);

    public AbstractChangeDetectionInitModule(
            MonitorTargetDefinitionProperty monitorTargetDefinitionProperty,
            WebpageService webpageService,
            ECSiteAccountRepository ecSiteAccountRepository,
            NormalDataRepository normalDataRepository,
            AbstractPurchaseHistoryModule purchaseHistoryModule,
            AbstractProductModule productModule
    ) {
        super(
                monitorTargetDefinitionProperty,
                webpageService,
                ecSiteAccountRepository,
                normalDataRepository,
                purchaseHistoryModule,
                productModule);
    }

    @Override
    public abstract String getModuleType();

    /**
     * Implementation of init method
     */
    @Override
    public void init(List<String> sites, String target) throws IOException {
        LOGGER.debug("[init]");
        this.processMonitorTarget();
    }

    /**
     * Save normal data in database
     *
     * @param normalData normal data as string
     * @param page       the page name
     * @param pageKey    the page key
     */
    protected void saveNormalData(String normalData, String pageKey, String page) {
        LOGGER.debug("[saveNormalData]");
        NormalDataDAO dao = normalDataRepository.findFirstByEcSiteAndPageAndPageKey(this.getModuleType(), page, pageKey);
        if (dao == null) {
            dao = new NormalDataDAO();
        }

        dao.setEcSite(this.getModuleType());
        dao.setNormalData(normalData);
        dao.setDownloadedAt(new Date());
        dao.setPage(page);
        dao.setPageKey(pageKey);
        normalDataRepository.save(dao);
    }

    /**
     * process purchase history crawler result
     *
     * @param crawlerResult the crawler result
     * @param pageKey       the page key
     */
    protected void processPurchaseHistory(AbstractPurchaseHistoryCrawlerResult crawlerResult, String pageKey) {
        LOGGER.debug("[processPurchaseHistory]");
        List<PurchaseHistory> purchaseHistoryList = crawlerResult.getPurchaseHistoryList();
        saveNormalData(PurchaseHistory.toArrayJson(purchaseHistoryList), pageKey, Consts.PURCHASE_HISTORY_LIST_PAGE_NAME);
    }

    /**
     * process product info crawler result
     *
     * @param crawlerResult the crawler result
     */
    protected void processProductInfo(AbstractProductCrawlerResult crawlerResult) {
        LOGGER.debug("[processProductInfo]");
        ProductInfo productInfo = crawlerResult.getProductInfo();
        saveNormalData(productInfo.toJson(), productInfo.getCode(), Consts.PRODUCT_DETAIL_PAGE_NAME);
    }
}
