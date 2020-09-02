package com.topcoder.scraper.module.ecunifiedmodule;

import com.topcoder.common.config.CheckItemsDefinitionProperty;
import com.topcoder.common.config.MonitorTargetDefinitionProperty;
import com.topcoder.common.dao.CheckResultDAO;
import com.topcoder.common.dao.NormalDataDAO;
import com.topcoder.common.model.Notification;
import com.topcoder.common.model.ProductCheckResultDetail;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.model.PurchaseHistoryCheckResultDetail;
import com.topcoder.common.repository.CheckResultRepository;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.NormalDataRepository;
import com.topcoder.common.util.CheckUtils;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.module.IChangeDetectionCheckModule;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductDetailCrawlerResult;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralPurchaseHistoryCrawlerResult;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * General implementation of ChangeDetectionInitModule
 */
@Component
public class GeneralChangeDetectionCheckModule extends GeneralChangeDetectionCommonModule implements IChangeDetectionCheckModule {

    private static Logger LOGGER = LoggerFactory.getLogger(GeneralChangeDetectionInitModule.class);

    protected final CheckItemsDefinitionProperty checkItemsDefinitionProperty;
    protected final CheckResultRepository checkResultRepository;

    public GeneralChangeDetectionCheckModule(
            MonitorTargetDefinitionProperty monitorTargetDefinitionProperty,
            WebpageService webpageService,
            ECSiteAccountRepository ecSiteAccountRepository,
            NormalDataRepository normalDataRepository,
            GeneralPurchaseHistoryModule purchaseHistoryModule,
            GeneralProductDetailModule productModule,
            CheckItemsDefinitionProperty checkItemsDefinitionProperty,
            CheckResultRepository checkResultRepository
    ) {
        super(
                monitorTargetDefinitionProperty,
                webpageService,
                ecSiteAccountRepository,
                normalDataRepository,
                purchaseHistoryModule,
                productModule
        );

        this.checkItemsDefinitionProperty = checkItemsDefinitionProperty;
        this.checkResultRepository = checkResultRepository;
    }

    /**
     * Implementation of check method
     */
    @Override
    public void check(List<String> sites) throws IOException {
        LOGGER.debug("[check]");
        this.processMonitorTarget(sites);
    }

    /**
     * Process purchase history crawler result
     *
     * @param site          ec site
     * @param crawlerResult the crawler result
     * @param pageKey       the page key
     */
    protected void processPurchaseHistory(String site, GeneralPurchaseHistoryCrawlerResult crawlerResult, String pageKey) {
        List<PurchaseHistory> purchaseHistoryList = crawlerResult.getPurchaseHistoryList();

        CheckItemsDefinitionProperty.CheckItemsCheckSite checkSiteDefinition = checkItemsDefinitionProperty.getCheckSiteDefinition(site);
        CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = checkSiteDefinition.getCheckPageDefinition(Consts.PURCHASE_HISTORY_LIST_PAGE_NAME);

        NormalDataDAO normalDataDAO = normalDataRepository.findFirstByEcSiteAndPageAndPageKey(site, Consts.PURCHASE_HISTORY_LIST_PAGE_NAME, pageKey);
        if (normalDataDAO == null) {
            // Could not find in database.
            // It's new product.
            LOGGER.warn(
                    String.format(
                            "Could not find %s (%s) in database, please run change_detection_init first. Skip.",
                            site, pageKey));
            return;
        }

        List<PurchaseHistory> dbPurchaseHistoryList = PurchaseHistory.fromJsonToList(normalDataDAO.getNormalData());

        List<PurchaseHistoryCheckResultDetail> results =
                CheckUtils.checkPurchaseHistoryList(checkItemsCheckPage, dbPurchaseHistoryList, purchaseHistoryList);

        boolean passed = results.stream().allMatch(r -> r.isOk());

        this.saveCheckResult(site, passed, PurchaseHistoryCheckResultDetail.toArrayJson(results), Consts.PURCHASE_HISTORY_LIST_PAGE_NAME, null);

        Notification notification = new Notification(site, Consts.PURCHASE_HISTORY_LIST_PAGE_NAME, pageKey);
        notification.setHtmlPaths(crawlerResult.getHtmlPathList());
        notification.setDetectionTime(new Date());
        webpageService.save("notification", site, notification.toString());
    }

    /**
     * Process product info crawler result
     *
     * @param site          ec site
     * @param crawlerResult the crawler result
     */
    protected void processProductInfo(String site, GeneralProductDetailCrawlerResult crawlerResult) {
        ProductInfo productInfo = crawlerResult.getProductInfo();

        CheckItemsDefinitionProperty.CheckItemsCheckSite checkSiteDefinition = checkItemsDefinitionProperty.getCheckSiteDefinition(site);
        CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = checkSiteDefinition.getCheckPageDefinition(Consts.PRODUCT_DETAIL_PAGE_NAME);
        NormalDataDAO normalDataDAO = normalDataRepository.findFirstByEcSiteAndPageAndPageKey(site, Consts.PRODUCT_DETAIL_PAGE_NAME, productInfo.getCode());

        if (normalDataDAO == null) {
            // Could not find in database.
            // It's new product.
            LOGGER.warn(
                    String.format(
                            "Could not find %s: %s in database, please run change_detection_init first. Skip.",
                            site, productInfo.getCode()));
            return;
        }

        ProductInfo dbProductInfo = ProductInfo.fromJson(normalDataDAO.getNormalData());
        ProductCheckResultDetail result = CheckUtils.checkProductInfo(checkItemsCheckPage, dbProductInfo, productInfo);
        this.saveCheckResult(site, result.isOk(), result.toJson(), Consts.PRODUCT_DETAIL_PAGE_NAME, productInfo.getCode());

        Notification notification = new Notification(site, Consts.PRODUCT_DETAIL_PAGE_NAME, productInfo.getCode());
        notification.addHtmlPath(crawlerResult.getHtmlPath());
        notification.setDetectionTime(new Date());
        webpageService.save("notification", site, notification.toString());
    }

    ;

    /**
     * Save check result in database
     *
     * @param site              ec site
     * @param passed            true if result is passed
     * @param checkResultDetail check result detail as string
     * @param page              the page name
     * @param pageKey           the page key
     */
    private void saveCheckResult(String site, boolean passed, String checkResultDetail, String page, String pageKey) {
        CheckResultDAO dao = checkResultRepository.findFirstByEcSiteAndPageAndPageKey(site, page, pageKey);
        if (dao == null) {
            dao = new CheckResultDAO();
        }

        dao.setEcSite(site);
        dao.setCheckResultDetail(checkResultDetail);
        dao.setCheckedAt(new Date());
        dao.setPage(page);
        dao.setPageKey(pageKey);
        dao.setTotalCheckStatus(passed ? "OK" : "NG");
        checkResultRepository.save(dao);
    }

}
