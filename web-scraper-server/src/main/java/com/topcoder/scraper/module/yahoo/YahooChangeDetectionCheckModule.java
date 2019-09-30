package com.topcoder.scraper.module.yahoo;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.CheckUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.topcoder.common.repository.NormalDataRepository;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.module.ChangeDetectionCheckModule;
import com.topcoder.scraper.module.yahoo.crawler.YahooAuthenticationCrawler;
import com.topcoder.scraper.module.yahoo.crawler.YahooProductDetailCrawler;
import com.topcoder.scraper.module.general.ProductDetailCrawlerResult;
import com.topcoder.scraper.module.yahoo.crawler.YahooPurchaseHistoryListCrawler;
import com.topcoder.scraper.module.PurchaseHistoryListCrawlerResult;
import com.topcoder.scraper.service.WebpageService;

@Component
public class YahooChangeDetectionCheckModule extends ChangeDetectionCheckModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooChangeDetectionCheckModule.class);
  MonitorTargetDefinitionProperty monitorTargetDefinitionProperty;
  CheckItemsDefinitionProperty checkItemsDefinitionProperty;
  WebpageService webpageService;
  CheckResultRepository checkResultRepository;
  NormalDataRepository normalDataRepository;

  @Autowired
  public YahooChangeDetectionCheckModule(
      MonitorTargetDefinitionProperty monitorTargetDefinitionProperty,
      CheckItemsDefinitionProperty checkItemsDefinitionProperty,
      WebpageService webpageService,
      CheckResultRepository checkResultRepository,
      NormalDataRepository normalDataRepository) {
    this.monitorTargetDefinitionProperty = monitorTargetDefinitionProperty;
    this.checkItemsDefinitionProperty = checkItemsDefinitionProperty;
    this.webpageService = webpageService;
    this.checkResultRepository = checkResultRepository;
    this.normalDataRepository = normalDataRepository;
  }

  @Override
  public String getECName() {
    return "yahoo";
  }

  @Override
  public void check() throws IOException {
    for (MonitorTargetDefinitionProperty.MonitorTargetCheckSite checkSite : monitorTargetDefinitionProperty.getCheckSites()) {
      if (!this.getECName().equalsIgnoreCase(checkSite.getEcSite())) {
        continue;
      }
      CheckItemsDefinitionProperty.CheckItemsCheckSite checkSiteDefinition = checkItemsDefinitionProperty.getCheckSiteDefinition(getECName());
      
      for (MonitorTargetDefinitionProperty.MonitorTargetCheckPage monitorTargetCheckPage : checkSite.getCheckPages()) {
        if (monitorTargetCheckPage.getPageName().equalsIgnoreCase(Consts.PURCHASE_HISTORY_LIST_PAGE_NAME)) {
          List<String> usernameList = monitorTargetCheckPage.getCheckTargetKeys();

          String passwordListString = System.getenv(Consts.YAHOO_CHECK_TARGET_KEYS_PASSWORDS); //TODO: GENERALIZE!
          if (passwordListString == null) {
            LOGGER.error("Please set environment variable YAHOO_CHECK_TARGET_KEYS_PASSWORDS first");//TODO: GENERALIZE!
            throw new RuntimeException("environment variable YAHOO_CHECK_TARGET_KEYS_PASSWORDS not set");//TODO: GENERALIZE!
          }
          List<String> passwordList = Arrays.asList(passwordListString.split(","));

          for (int i = 0; i < usernameList.size(); i++) {
            String username = usernameList.get(i);
            String password = passwordList.get(i);

            TrafficWebClient webClient = new TrafficWebClient(0, false);
            YahooAuthenticationCrawler authenticationCrawler = new YahooAuthenticationCrawler(getECName(), webpageService);//TODO: GENERALIZE!
            if (!authenticationCrawler.authenticate(webClient, username, password)) {
              LOGGER.error(String.format("Failed to login %s with username %s. Skip.", getECName(), username));
              continue;
            }
            
            //TODO: GENERALIZE!
            YahooPurchaseHistoryListCrawler purchaseHistoryListCrawler = new YahooPurchaseHistoryListCrawler(getECName(), webpageService, username, password);
            PurchaseHistoryListCrawlerResult crawlerResult = purchaseHistoryListCrawler.fetchPurchaseHistoryList(webClient, null, true);
            webClient.finishTraffic();
            
            processPurchaseHistory(crawlerResult, username, checkSiteDefinition);
          }

        } else if (monitorTargetCheckPage.getPageName().equalsIgnoreCase(Consts.PRODUCT_DETAIL_PAGE_NAME)) {
          YahooProductDetailCrawler crawler = new YahooProductDetailCrawler(getECName(), webpageService);
          for (String productCode : monitorTargetCheckPage.getCheckTargetKeys()) {
            TrafficWebClient webClient = new TrafficWebClient(0, false);
            ProductDetailCrawlerResult crawlerResult = crawler.fetchProductInfo(webClient, productCode, true);
            webClient.finishTraffic();

            processProductInfo(crawlerResult, checkSiteDefinition);
          }

        } else {
          throw new RuntimeException("Unknown monitor target definition " + monitorTargetCheckPage.getPageName());
        }
      }
    }
  }


    /**
   * Process purchase history crawler result
   * @param crawlerResult the crawler result
   * @param pageKey the page key
   */
  protected void processPurchaseHistory(PurchaseHistoryListCrawlerResult crawlerResult, String pageKey, CheckItemsDefinitionProperty.CheckItemsCheckSite checkSiteDefinition) {
    List<PurchaseHistory> purchaseHistoryList = crawlerResult.getPurchaseHistoryList();

    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = checkSiteDefinition.getCheckPageDefinition(Consts.PURCHASE_HISTORY_LIST_PAGE_NAME);

    NormalDataDAO normalDataDAO = normalDataRepository.findFirstByEcSiteAndPageAndPageKey(getECName(), Consts.PURCHASE_HISTORY_LIST_PAGE_NAME, pageKey);
    if (normalDataDAO == null) {
      // Could not find in database.
      // It's new product.
      LOGGER.warn(
        String.format(
          "Could not find %s (%s) in database, please run change_detection_init first. Skip.",
          getECName(), pageKey));
      return;
    }

    List<PurchaseHistory> dbPurchaseHistoryList = PurchaseHistory.fromJsonToList(normalDataDAO.getNormalData());

    List<PurchaseHistoryCheckResultDetail> results =
      CheckUtils.checkPurchaseHistoryList(checkItemsCheckPage, dbPurchaseHistoryList, purchaseHistoryList);

    boolean passed = results.stream().allMatch(r -> r.isOk());

    saveCheckResult(passed, PurchaseHistoryCheckResultDetail.toArrayJson(results), Consts.PURCHASE_HISTORY_LIST_PAGE_NAME, null);

    Notification notification = new Notification(getECName(), Consts.PURCHASE_HISTORY_LIST_PAGE_NAME, pageKey);
    notification.setHtmlPaths(crawlerResult.getHtmlPathList());
    notification.setDetectionTime(new Date());
    webpageService.save("notification", getECName(), notification.toString());
  }

  /**
   * Process product info crawler result
   * @param crawlerResult the crawler result
   */
  protected void processProductInfo(ProductDetailCrawlerResult crawlerResult, CheckItemsDefinitionProperty.CheckItemsCheckSite checkSiteDefinition) {
    ProductInfo productInfo = crawlerResult.getProductInfo();
    
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = checkSiteDefinition.getCheckPageDefinition(Consts.PRODUCT_DETAIL_PAGE_NAME);
    NormalDataDAO normalDataDAO = normalDataRepository.findFirstByEcSiteAndPageAndPageKey(getECName(), Consts.PRODUCT_DETAIL_PAGE_NAME, productInfo.getCode());

    if (normalDataDAO == null) {
      // Could not find in database.
      // It's new product.
      LOGGER.warn(
        String.format(
          "Could not find %s: %s in database, please run change_detection_init first. Skip.",
          getECName(), productInfo.getCode()));
      return;
    }

    ProductInfo dbProductInfo = ProductInfo.fromJson(normalDataDAO.getNormalData());
    ProductCheckResultDetail result = CheckUtils.checkProductInfo(checkItemsCheckPage, dbProductInfo, productInfo);
    saveCheckResult(result.isOk(), result.toJson(), Consts.PRODUCT_DETAIL_PAGE_NAME, productInfo.getCode());

    Notification notification = new Notification(getECName(), Consts.PRODUCT_DETAIL_PAGE_NAME, productInfo.getCode());
    notification.addHtmlPath(crawlerResult.getHtmlPath());
    notification.setDetectionTime(new Date());
    webpageService.save("notification", getECName(), notification.toString());
  };

  /**
   * Save check result in database
   * @param passed true if result is passed
   * @param checkResultDetail check result detail as string
   * @param page the page name
   * @param pageKey the page key
   */
  protected void saveCheckResult(boolean passed, String checkResultDetail, String page, String pageKey) {
    CheckResultDAO dao = checkResultRepository.findFirstByEcSiteAndPageAndPageKey(getECName(), page, pageKey);
    if (dao == null) {
      dao = new CheckResultDAO();
    }

    dao.setEcSite(getECName());
    dao.setCheckResultDetail(checkResultDetail);
    dao.setCheckedAt(new Date());
    dao.setPage(page);
    dao.setPageKey(pageKey);
    dao.setTotalCheckStatus(passed ? "OK" : "NG");
    checkResultRepository.save(dao);
  }
  
}
