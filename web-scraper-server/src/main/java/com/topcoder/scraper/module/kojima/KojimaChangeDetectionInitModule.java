package com.topcoder.scraper.module.kojima;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.topcoder.common.traffic.TrafficWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.config.MonitorTargetDefinitionProperty;
import com.topcoder.common.repository.NormalDataRepository;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.module.amazon.AmazonChangeDetectionInitModule;
import com.topcoder.scraper.module.kojima.crawler.KojimaAuthenticationCrawler;
import com.topcoder.scraper.module.kojima.crawler.KojimaProductDetailCrawler;
import com.topcoder.scraper.module.kojima.crawler.KojimaProductDetailCrawlerResult;
import com.topcoder.scraper.module.kojima.crawler.KojimaPurchaseHistoryListCrawler;
import com.topcoder.scraper.module.kojima.crawler.KojimaPurchaseHistoryListCrawlerResult;
import com.topcoder.scraper.service.WebpageService;

@Component
public class KojimaChangeDetectionInitModule extends AmazonChangeDetectionInitModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaChangeDetectionInitModule.class);

  @Autowired
  public KojimaChangeDetectionInitModule(
      MonitorTargetDefinitionProperty monitorTargetDefinitionProperty,
      WebpageService webpageService,
      NormalDataRepository repository) {
    super(new AmazonProperty(), monitorTargetDefinitionProperty, webpageService, repository);
  }

  @Override
  public String getECName() {
    return "kojima";
  }

  @Override
  public void init() throws IOException {
    for(MonitorTargetDefinitionProperty.MonitorTargetCheckSite monitorTargetCheckSite : monitorTargetDefinitionProperty.getCheckSites()) {
      if (!this.getECName().equalsIgnoreCase(monitorTargetCheckSite.getEcSite())) {
        continue;
      }
      for (MonitorTargetDefinitionProperty.MonitorTargetCheckPage monitorTargetCheckPage :monitorTargetCheckSite.getCheckPages()) {
        if (monitorTargetCheckPage.getPageName().equalsIgnoreCase(Consts.PURCHASE_HISTORY_LIST_PAGE_NAME)) {
          List<String> usernameList = monitorTargetCheckPage.getCheckTargetKeys();

          String passwordListString = System.getenv(Consts.KOJIMA_CHECK_TARGET_KEYS_PASSWORDS);
          if (passwordListString == null) {
            LOGGER.error("Please set environment variable KOJIMA_CHECK_TARGET_KEYS_PASSWORDS first");
            throw new RuntimeException("environment variable KOJIMA_CHECK_TARGET_KEYS_PASSWORDS not set");
          }
          List<String> passwordList = Arrays.asList(passwordListString.split(","));

          for (int i = 0; i < usernameList.size(); i++) {
            String username = usernameList.get(i);
            String password = passwordList.get(i);

            LOGGER.info("init ...");
            KojimaAuthenticationCrawler authenticationCrawler = new KojimaAuthenticationCrawler(getECName(), webpageService);
            TrafficWebClient webClient = new TrafficWebClient(0, false);
            if (!authenticationCrawler.authenticate(webClient, username, password)) {
              LOGGER.error(String.format("Failed to login %s with username %s. Skip.", getECName(), username));
              continue;
            }

            KojimaPurchaseHistoryListCrawler purchaseHistoryListCrawler = new KojimaPurchaseHistoryListCrawler(getECName(), webpageService);
            KojimaPurchaseHistoryListCrawlerResult crawlerResult = purchaseHistoryListCrawler.fetchPurchaseHistoryList(webClient, null, false);
            webClient.finishTraffic();

            processPurchaseHistory(crawlerResult, username);
          }

        } else if (monitorTargetCheckPage.getPageName().equalsIgnoreCase(Consts.PRODUCT_DETAIL_PAGE_NAME)) {
          KojimaProductDetailCrawler crawler = new KojimaProductDetailCrawler(getECName(), webpageService);
          for (String productCode : monitorTargetCheckPage.getCheckTargetKeys()) {
            TrafficWebClient webClient = new TrafficWebClient(0, false);
            KojimaProductDetailCrawlerResult crawlerResult = crawler.fetchProductInfo(webClient, productCode, false);
            webClient.finishTraffic();

            processProductInfo(crawlerResult);
          }

        } else {
          throw new RuntimeException("Unknown monitor target definition " + monitorTargetCheckPage.getPageName());
        }

      }
    }
  }

}
