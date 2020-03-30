package com.topcoder.scraper.module.ecunifiedmodule;

import com.topcoder.common.config.MonitorTargetDefinitionProperty;
import com.topcoder.common.dao.NormalDataDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.NormalDataRepository;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.module.IChangeDetectionInitModule;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductCrawlerResult;
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
public class GeneralChangeDetectionInitModule extends GeneralChangeDetectionCommonModule implements IChangeDetectionInitModule {

  private static Logger LOGGER = LoggerFactory.getLogger(GeneralChangeDetectionInitModule.class);

  public GeneralChangeDetectionInitModule(
          MonitorTargetDefinitionProperty monitorTargetDefinitionProperty,
          WebpageService                  webpageService,
          ECSiteAccountRepository         ecSiteAccountRepository,
          NormalDataRepository            normalDataRepository,
          GeneralPurchaseHistoryModule    purchaseHistoryModule,
          GeneralProductModule            productModule
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

  /**
   * Implementation of init method
   */
  @Override
  public void init(List<String> sites) throws IOException {
    LOGGER.debug("[init]");
    this.processMonitorTarget(sites);
  }

  /**
   * Save normal data in database
   * @param site ec site
   * @param normalData normal data as string
   * @param page the page name
   * @param pageKey the page key
   */
  protected void saveNormalData(String site, String normalData, String pageKey, String page) {
    LOGGER.debug("[saveNormalData]");
    NormalDataDAO dao = normalDataRepository.findFirstByEcSiteAndPageAndPageKey(site, page, pageKey);
    if (dao == null) {
      dao = new NormalDataDAO();
    }

    dao.setEcSite(site);
    dao.setNormalData(normalData);
    dao.setDownloadedAt(new Date());
    dao.setPage(page);
    dao.setPageKey(pageKey);
    normalDataRepository.save(dao);
  }

  /**
   * process purchase history crawler result
   * @param site ec site
   * @param crawlerResult the crawler result
   * @param pageKey the page key
   */
  protected void processPurchaseHistory(String site, GeneralPurchaseHistoryCrawlerResult crawlerResult, String pageKey) {
    LOGGER.debug("[processPurchaseHistory]");
    List<PurchaseHistory> purchaseHistoryList = crawlerResult.getPurchaseHistoryList();
    saveNormalData(site, PurchaseHistory.toArrayJson(purchaseHistoryList), pageKey, Consts.PURCHASE_HISTORY_LIST_PAGE_NAME);
  }

  /**
   * process product info crawler result
   * @param site ec site
   * @param crawlerResult the crawler result
   */
  protected void processProductInfo(String site, GeneralProductCrawlerResult crawlerResult) {
    LOGGER.debug("[processProductInfo]");
    ProductInfo productInfo = crawlerResult.getProductInfo();
    saveNormalData(site, productInfo.toJson(), productInfo.getCode(), Consts.PRODUCT_DETAIL_PAGE_NAME);
  }
}
