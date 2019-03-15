package com.topcoder.scraper.module.amazon;

import com.gargoylesoftware.htmlunit.WebClient;
import com.topcoder.api.dao.ECSiteAccountDAO;
import com.topcoder.api.entity.AuthStatusType;
import com.topcoder.api.repository.ECSiteAccountRepository;
import com.topcoder.api.util.Common;
import com.topcoder.scraper.config.AmazonProperty;
import com.topcoder.scraper.model.PurchaseHistory;
import com.topcoder.scraper.module.PurchaseHistoryListModule;
import com.topcoder.scraper.module.amazon.crawler.AmazonPurchaseHistoryListCrawler;
import com.topcoder.scraper.module.amazon.crawler.AmazonPurchaseHistoryListCrawlerResult;
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
  private final WebClient webClient;
  private final PurchaseHistoryService purchaseHistoryService;
  private final WebpageService webpageService;
  private final ECSiteAccountRepository ecSiteAccountRepository;

  @Autowired
  public AmazonPurchaseHistoryListModule(
    AmazonProperty property,
    WebClient webClient,
    PurchaseHistoryService purchaseHistoryService,
    ECSiteAccountRepository ecSiteAccountRepository,
    WebpageService webpageService) {
    this.property = property;
    this.webClient = webClient;
    this.purchaseHistoryService = purchaseHistoryService;
    this.webpageService = webpageService;
    this.ecSiteAccountRepository = ecSiteAccountRepository;
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

    Optional<PurchaseHistory> lastPurchaseHistory = purchaseHistoryService.fetchLast(getECName());

    LOGGER.info("web client version = " + webClient.getBrowserVersion());

    Iterable<ECSiteAccountDAO> accountDAOS = ecSiteAccountRepository.findAll();
    for (ECSiteAccountDAO ecSiteAccountDAO : accountDAOS) {

      boolean restoreRet = Common.restoreCookies(webClient, ecSiteAccountDAO);
      if (!restoreRet) {
        LOGGER.error("skip ecSite id = " + ecSiteAccountDAO.getId() + ", restore cookies failed");
        continue;
      }

      try {
        AmazonPurchaseHistoryListCrawler crawler = new AmazonPurchaseHistoryListCrawler(getECName(), property, webpageService);

        AmazonPurchaseHistoryListCrawlerResult crawlerResult = crawler.fetchPurchaseHistoryList(webClient, lastPurchaseHistory.orElse(null), true);
        List<PurchaseHistory> list = crawlerResult.getPurchaseHistoryList();

        list.forEach(purchaseHistory -> purchaseHistory.setEcSiteAccountId(ecSiteAccountDAO.getId()));
        purchaseHistoryService.save(getECName(), list);
        LOGGER.info("succeed fetch purchaseHistory for ecSite id = " + ecSiteAccountDAO.getId());
      } catch (Exception e) { // here catch all exception and did not throw it
        ecSiteAccountDAO.setAuthStatus(AuthStatusType.FAILED);
        ecSiteAccountDAO.setAuthFailReason("Cookie expires"); // if error here, i think the only reason is cookie expires
        ecSiteAccountRepository.save(ecSiteAccountDAO);
        LOGGER.error("failed to PurchaseHistory for ecSite id = " + ecSiteAccountDAO.getId());
        e.printStackTrace();
      }
    }

  }


}
