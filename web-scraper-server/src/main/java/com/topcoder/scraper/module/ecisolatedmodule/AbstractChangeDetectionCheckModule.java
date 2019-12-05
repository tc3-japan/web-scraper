package com.topcoder.scraper.module.ecisolatedmodule;

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
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractProductCrawlerResult;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractPurchaseHistoryCrawlerResult;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Abstract class for ChangeDetectionInitModule
 */
public abstract class AbstractChangeDetectionCheckModule extends AbstractChangeDetectionCommonModule implements IChangeDetectionCheckModule {

  private static Logger LOGGER = LoggerFactory.getLogger(AbstractChangeDetectionInitModule.class);

  protected final CheckItemsDefinitionProperty checkItemsDefinitionProperty;
  protected final CheckResultRepository        checkResultRepository;

  public AbstractChangeDetectionCheckModule(
          MonitorTargetDefinitionProperty    monitorTargetDefinitionProperty,
          WebpageService                     webpageService,
          ECSiteAccountRepository            ecSiteAccountRepository,
          NormalDataRepository               normalDataRepository,
          AbstractPurchaseHistoryListModule  purchaseHistoryListModule,
          AbstractProductModule              productModule,
          CheckItemsDefinitionProperty       checkItemsDefinitionProperty,
          CheckResultRepository              checkResultRepository
  ) {
    super(
            monitorTargetDefinitionProperty,
            webpageService,
            ecSiteAccountRepository,
            normalDataRepository,
            purchaseHistoryListModule,
            productModule);

    this.checkItemsDefinitionProperty = checkItemsDefinitionProperty;
    this.checkResultRepository        = checkResultRepository;
  }

  @Override
  public abstract String getModuleType();

  /**
   * Implementation of check method
   */
  @Override
  public void check(List<String> sites) throws IOException {
    LOGGER.info("[check]");
    this.processMonitorTarget();
  }

  /**
   * Process purchase history crawler result
   * @param crawlerResult the crawler result
   * @param pageKey the page key
   */
  protected void processPurchaseHistory(AbstractPurchaseHistoryCrawlerResult crawlerResult, String pageKey) {
    List<PurchaseHistory> purchaseHistoryList = crawlerResult.getPurchaseHistoryList();

    CheckItemsDefinitionProperty.CheckItemsCheckSite checkSiteDefinition = checkItemsDefinitionProperty.getCheckSiteDefinition(getModuleType());
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = checkSiteDefinition.getCheckPageDefinition(Consts.PURCHASE_HISTORY_LIST_PAGE_NAME);

    NormalDataDAO normalDataDAO = normalDataRepository.findFirstByEcSiteAndPageAndPageKey(getModuleType(), Consts.PURCHASE_HISTORY_LIST_PAGE_NAME, pageKey);
    if (normalDataDAO == null) {
      // Could not find in database.
      // It's new product.
      LOGGER.warn(
              String.format(
                      "Could not find %s (%s) in database, please run change_detection_init first. Skip.",
                      getModuleType(), pageKey));
      return;
    }

    List<PurchaseHistory> dbPurchaseHistoryList = PurchaseHistory.fromJsonToList(normalDataDAO.getNormalData());

    List<PurchaseHistoryCheckResultDetail> results =
            CheckUtils.checkPurchaseHistoryList(checkItemsCheckPage, dbPurchaseHistoryList, purchaseHistoryList);

    boolean passed = results.stream().allMatch(r -> r.isOk());

    this.saveCheckResult(passed, PurchaseHistoryCheckResultDetail.toArrayJson(results), Consts.PURCHASE_HISTORY_LIST_PAGE_NAME, null);

    Notification notification = new Notification(getModuleType(), Consts.PURCHASE_HISTORY_LIST_PAGE_NAME, pageKey);
    notification.setHtmlPaths(crawlerResult.getHtmlPathList());
    notification.setDetectionTime(new Date());
    webpageService.save("notification", getModuleType(), notification.toString());
  }

  /**
   * Process product info crawler result
   * @param crawlerResult the crawler result
   */
  protected void processProductInfo(AbstractProductCrawlerResult crawlerResult) {
    ProductInfo productInfo = crawlerResult.getProductInfo();

    CheckItemsDefinitionProperty.CheckItemsCheckSite checkSiteDefinition = checkItemsDefinitionProperty.getCheckSiteDefinition(getModuleType());
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = checkSiteDefinition.getCheckPageDefinition(Consts.PRODUCT_DETAIL_PAGE_NAME);
    NormalDataDAO normalDataDAO = normalDataRepository.findFirstByEcSiteAndPageAndPageKey(getModuleType(), Consts.PRODUCT_DETAIL_PAGE_NAME, productInfo.getCode());

    if (normalDataDAO == null) {
      // Could not find in database.
      // It's new product.
      LOGGER.warn(
              String.format(
                      "Could not find %s: %s in database, please run change_detection_init first. Skip.",
                      getModuleType(), productInfo.getCode()));
      return;
    }

    ProductInfo dbProductInfo = ProductInfo.fromJson(normalDataDAO.getNormalData());
    ProductCheckResultDetail result = CheckUtils.checkProductInfo(checkItemsCheckPage, dbProductInfo, productInfo);
    this.saveCheckResult(result.isOk(), result.toJson(), Consts.PRODUCT_DETAIL_PAGE_NAME, productInfo.getCode());

    Notification notification = new Notification(getModuleType(), Consts.PRODUCT_DETAIL_PAGE_NAME, productInfo.getCode());
    notification.addHtmlPath(crawlerResult.getHtmlPath());
    notification.setDetectionTime(new Date());
    webpageService.save("notification", getModuleType(), notification.toString());
  };

  /**
   * Save check result in database
   * @param passed true if result is passed
   * @param checkResultDetail check result detail as string
   * @param page the page name
   * @param pageKey the page key
   */
  private void saveCheckResult(boolean passed, String checkResultDetail, String page, String pageKey) {
    CheckResultDAO dao = checkResultRepository.findFirstByEcSiteAndPageAndPageKey(getModuleType(), page, pageKey);
    if (dao == null) {
      dao = new CheckResultDAO();
    }

    dao.setEcSite(getModuleType());
    dao.setCheckResultDetail(checkResultDetail);
    dao.setCheckedAt(new Date());
    dao.setPage(page);
    dao.setPageKey(pageKey);
    dao.setTotalCheckStatus(passed ? "OK" : "NG");
    checkResultRepository.save(dao);
  }

}
