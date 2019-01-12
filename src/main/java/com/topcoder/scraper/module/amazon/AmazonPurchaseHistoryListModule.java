package com.topcoder.scraper.module.amazon;

import com.gargoylesoftware.htmlunit.WebClient;
import com.topcoder.scraper.config.AmazonProperty;
import com.topcoder.scraper.exception.FetchPurchaseHistoryListFailure;
import com.topcoder.scraper.model.PurchaseHistory;
import com.topcoder.scraper.module.PurchaseHistoryListModule;
import com.topcoder.scraper.module.amazon.crawler.AmazonAuthenticationCrawler;
import com.topcoder.scraper.module.amazon.crawler.AmazonAuthenticationCrawlerResult;
import com.topcoder.scraper.module.amazon.crawler.AmazonPurchaseHistoryListCrawler;
import com.topcoder.scraper.module.amazon.crawler.AmazonPurchaseHistoryListCrawlerResult;
import com.topcoder.scraper.service.PurchaseHistoryService;
import com.topcoder.scraper.service.WebpageService;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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


  @Autowired
  public AmazonPurchaseHistoryListModule(
    AmazonProperty property,
    WebClient webClient,
    PurchaseHistoryService purchaseHistoryService,
    WebpageService webpageService) {
    this.property = property;
    this.webClient = webClient;
    this.purchaseHistoryService = purchaseHistoryService;
    this.webpageService = webpageService;
  }

  @Override
  public String getECName() {
    return "amazon";
  }

  /**
   * Implementation of fetchPurchaseHistoryList method
   */
  @Override
  public void fetchPurchaseHistoryList() throws IOException {

    Optional<PurchaseHistory> lastPurchaseHistory = purchaseHistoryService.fetchLast(getECName());

    AmazonAuthenticationCrawler authenticationCrawler = new AmazonAuthenticationCrawler(getECName(), property, webpageService);
    AmazonAuthenticationCrawlerResult loginResult = authenticationCrawler.authenticate(webClient, property.getUsername(), property.getPassword());
    if (!loginResult.isSuccess()) {
      LOGGER.warn(String.format("Fail to login to %s %s", getECName(), property.getUsername()));
      throw new FetchPurchaseHistoryListFailure("Login failure");
    }

    AmazonPurchaseHistoryListCrawler crawler = new AmazonPurchaseHistoryListCrawler(getECName(), property, webpageService);

    AmazonPurchaseHistoryListCrawlerResult crawlerResult = crawler.fetchPurchaseHistoryList(webClient, lastPurchaseHistory.orElse(null), true);
    List<PurchaseHistory> list = crawlerResult.getPurchaseHistoryList();

    purchaseHistoryService.save(getECName(), list);
  }


}
