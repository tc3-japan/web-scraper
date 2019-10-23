package com.topcoder.scraper.module.ecisolatedmodule.yahoo;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.topcoder.common.traffic.TrafficWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.config.MonitorTargetDefinitionProperty;
import com.topcoder.common.dao.NormalDataDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.repository.NormalDataRepository;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.module.IChangeDetectionInitModule;
import com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler.YahooAuthenticationCrawler;
import com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler.YahooProductDetailCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductDetailCrawlerResult;
import com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler.YahooPurchaseHistoryListCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralPurchaseHistoryListCrawlerResult;
import com.topcoder.scraper.service.WebpageService;

@Component
public class YahooChangeDetectionInitModule extends IChangeDetectionInitModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooChangeDetectionInitModule.class);
  MonitorTargetDefinitionProperty monitorTargetDefinitionProperty;
  WebpageService webpageService;
  NormalDataRepository repository;

  @Autowired
  public YahooChangeDetectionInitModule(
      MonitorTargetDefinitionProperty monitorTargetDefinitionProperty,
      WebpageService webpageService,
      NormalDataRepository repository) {
    this.monitorTargetDefinitionProperty = monitorTargetDefinitionProperty;
    this.webpageService = webpageService;
    this.repository = repository;
  }

  @Override
  public String getModuleType() {
    return "yahoo";
  }

  @Override
  public void init(List<String> sites) throws IOException {
    for(MonitorTargetDefinitionProperty.MonitorTargetCheckSite monitorTargetCheckSite : monitorTargetDefinitionProperty.getCheckSites()) {
      if (!this.getModuleType().equalsIgnoreCase(monitorTargetCheckSite.getEcSite())) {
        continue;
      }
      for (MonitorTargetDefinitionProperty.MonitorTargetCheckPage monitorTargetCheckPage :monitorTargetCheckSite.getCheckPages()) {
        if (monitorTargetCheckPage.getPageName().equalsIgnoreCase(Consts.PURCHASE_HISTORY_LIST_PAGE_NAME)) {
          List<String> usernameList = monitorTargetCheckPage.getCheckTargetKeys();

          String passwordListString = System.getenv(Consts.YAHOO_CHECK_TARGET_KEYS_PASSWORDS);
          if (passwordListString == null) {
            LOGGER.error("Please set environment variable YAHOO_CHECK_TARGET_KEYS_PASSWORDS first"); //TODO: GENERALIZE!
            throw new RuntimeException("environment variable YAHOO_CHECK_TARGET_KEYS_PASSWORDS not set"); //TODO: GENERALIZE!
          }
          List<String> passwordList = Arrays.asList(passwordListString.split(","));

          for (int i = 0; i < usernameList.size(); i++) {
            String username = usernameList.get(i);
            String password = passwordList.get(i);

            LOGGER.info("init ...");
            YahooAuthenticationCrawler authenticationCrawler = new YahooAuthenticationCrawler(getModuleType(), webpageService);
            TrafficWebClient webClient = new TrafficWebClient(0, false);
            if (!authenticationCrawler.authenticate(webClient, username, password)) {
              LOGGER.error(String.format("Failed to login %s with username %s. Skip.", getModuleType(), username));
              continue;
            }

            YahooPurchaseHistoryListCrawler purchaseHistoryListCrawler = new YahooPurchaseHistoryListCrawler(getModuleType(), webpageService, username, password);
            GeneralPurchaseHistoryListCrawlerResult crawlerResult = purchaseHistoryListCrawler.fetchPurchaseHistoryList(webClient, null, false);
            webClient.finishTraffic();

            processPurchaseHistory(crawlerResult, username);
          }

        } else if (monitorTargetCheckPage.getPageName().equalsIgnoreCase(Consts.PRODUCT_DETAIL_PAGE_NAME)) {
          YahooProductDetailCrawler crawler = new YahooProductDetailCrawler(getModuleType(), webpageService);
          for (String productCode : monitorTargetCheckPage.getCheckTargetKeys()) {
            TrafficWebClient webClient = new TrafficWebClient(0, false);

            GeneralProductDetailCrawlerResult crawlerResult = crawler.fetchProductInfo(webClient, productCode, false);
            webClient.finishTraffic();

            processProductInfo(crawlerResult);
          }

        } else {
          throw new RuntimeException("Unknown monitor target definition " + monitorTargetCheckPage.getPageName());
        }

      }
    }
  }

  /**
   * process purchase history crawler result
   * @param crawlerResult the crawler result
   * @param pageKey the page key
   */
  protected void processPurchaseHistory(GeneralPurchaseHistoryListCrawlerResult crawlerResult, String pageKey) {
    List<PurchaseHistory> purchaseHistoryList = crawlerResult.getPurchaseHistoryList();
    saveNormalData(PurchaseHistory.toArrayJson(purchaseHistoryList), pageKey, Consts.PURCHASE_HISTORY_LIST_PAGE_NAME);
  }

  /**
   * process product info crawler result
   * @param crawlerResult the crawler result
   */
  protected void processProductInfo(GeneralProductDetailCrawlerResult crawlerResult) {
    ProductInfo productInfo = crawlerResult.getProductInfo();
    saveNormalData(productInfo.toJson(), productInfo.getCode(), Consts.PRODUCT_DETAIL_PAGE_NAME);
  }

    /**
   * Save normal data in database
   * @param normalData normal data as string
   * @param page the page name
   * @param pageKey the page key
   */
  protected void saveNormalData(String normalData, String pageKey, String page) {
    NormalDataDAO dao = repository.findFirstByEcSiteAndPageAndPageKey(getModuleType(), page, pageKey);
    if (dao == null) {
      dao = new NormalDataDAO();
    }

    dao.setEcSite(getModuleType());
    dao.setNormalData(normalData);
    dao.setDownloadedAt(new Date());
    dao.setPage(page);
    dao.setPageKey(pageKey);
    repository.save(dao);
  }
}
