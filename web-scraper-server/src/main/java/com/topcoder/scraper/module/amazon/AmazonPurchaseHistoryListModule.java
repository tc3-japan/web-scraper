package com.topcoder.scraper.module.amazon;

import com.topcoder.api.service.login.amazon.AmazonLoginHandler;
import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.Common;
import com.topcoder.scraper.module.PurchaseHistoryListModule;
import com.topcoder.scraper.module.amazon.crawler.AmazonPurchaseHistoryListCrawler;
import com.topcoder.scraper.module.PurchaseHistoryListCrawlerResult;
import com.topcoder.scraper.service.PurchaseHistoryService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Amazon implementation of PurchaseHistoryListModule
 */
@Component
public class AmazonPurchaseHistoryListModule extends PurchaseHistoryListModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonPurchaseHistoryListModule.class);

  private final AmazonProperty property;
  private final PurchaseHistoryService purchaseHistoryService;
  private final WebpageService webpageService;
  private final ECSiteAccountRepository ecSiteAccountRepository;
  private final AmazonLoginHandler loginHandler;

  @Autowired
  public AmazonPurchaseHistoryListModule(
    AmazonProperty property,
    PurchaseHistoryService purchaseHistoryService,
    ECSiteAccountRepository ecSiteAccountRepository,
    WebpageService webpageService,
    AmazonLoginHandler loginHandler) {
    this.property = property;
    this.purchaseHistoryService = purchaseHistoryService;
    this.webpageService = webpageService;
    this.ecSiteAccountRepository = ecSiteAccountRepository;
    this.loginHandler = loginHandler;
  }

  @Override
  public String getECName() {
    return "amazon";
  }

  /**
   * Implementation of fetchPurchaseHistoryList method
   */
  @Override
  public void fetchPurchaseHistoryList() {

    Iterable<ECSiteAccountDAO> accountDAOS = ecSiteAccountRepository.findAllByEcSite(getECName());
    for (ECSiteAccountDAO ecSiteAccountDAO : accountDAOS) {
      
      if (ecSiteAccountDAO.getEcUseFlag() != Boolean.TRUE) {
        LOGGER.info("EC Site [" + ecSiteAccountDAO.getId() + ":" + ecSiteAccountDAO.getEcSite() + "] is not active. Skipped.");
        continue;
      }
      Optional<PurchaseHistory> lastPurchaseHistory = purchaseHistoryService.fetchLast(ecSiteAccountDAO.getId());

      TrafficWebClient webClient = new TrafficWebClient(ecSiteAccountDAO.getUserId(), true);
      LOGGER.info("web client version = " + webClient.getWebClient().getBrowserVersion());
      boolean restoreRet = Common.restoreCookies(webClient.getWebClient(), ecSiteAccountDAO);
      if (!restoreRet) {
        LOGGER.error("skip ecSite id = " + ecSiteAccountDAO.getId() + ", restore cookies failed");
        continue;
      }

      try {
        AmazonPurchaseHistoryListCrawler crawler = new AmazonPurchaseHistoryListCrawler(getECName(), property, webpageService);

        PurchaseHistoryListCrawlerResult crawlerResult = crawler.fetchPurchaseHistoryList(webClient, lastPurchaseHistory.orElse(null), true);
        webClient.finishTraffic();
        List<PurchaseHistory> list = crawlerResult.getPurchaseHistoryList();

        if (list != null && list.size() > 0) {
          list.forEach(purchaseHistory -> purchaseHistory.setAccountId(Integer.toString(ecSiteAccountDAO.getId())));
          purchaseHistoryService.save(getECName(), list);
        }
        LOGGER.info("succeed fetch purchaseHistory for ecSite id = " + ecSiteAccountDAO.getId());
      } catch (Exception e) { // here catch all exception and did not throw it
        loginHandler.saveFailedResult(ecSiteAccountDAO, e.getMessage());
        LOGGER.error("failed to PurchaseHistory for ecSite id = " + ecSiteAccountDAO.getId());
        e.printStackTrace();
      }
    }
  }
}
