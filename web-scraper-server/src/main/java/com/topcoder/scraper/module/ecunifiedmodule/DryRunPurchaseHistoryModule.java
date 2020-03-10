package com.topcoder.scraper.module.ecunifiedmodule;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.api.service.login.LoginHandlerBase;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.Common;
import com.topcoder.scraper.module.IPurchaseHistoryModule;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralPurchaseHistoryCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralPurchaseHistoryCrawlerResult;
import com.topcoder.scraper.service.PurchaseHistoryService;
import com.topcoder.scraper.service.WebpageService;

/**
 * General implementation of ecisolatedmodule .. PurchaseHistoryModule
 */
// TODO: refactoring to imitate AbstractPurchaseHistoryModule
@Component
public class DryRunPurchaseHistoryModule implements IPurchaseHistoryModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPurchaseHistoryModule.class);

  private final PurchaseHistoryService purchaseHistoryService;
  private final WebpageService webpageService;
  private final ECSiteAccountRepository ecSiteAccountRepository;

  private String script;
  private List<PurchaseHistory> list;

  // TODO: arrange login handler
  //private final LoginHandlerBase loginHandler;

  @Autowired
  public DryRunPurchaseHistoryModule(PurchaseHistoryService purchaseHistoryService, ECSiteAccountRepository ecSiteAccountRepository, WebpageService webpageService
                                      //LoginHandlerBase loginHandler
  ) {
    this.purchaseHistoryService = purchaseHistoryService;
    this.webpageService = webpageService;
    this.ecSiteAccountRepository = ecSiteAccountRepository;
    // TODO: arrange login handler
    //this.loginHandler = loginHandler;
  }

  public void setScript(String script) {
	this.script = script;
  }

  @Override
  public String getModuleType() {
    return "general";
  }

  @Override
  public void fetchPurchaseHistoryList(List<String> sites) throws IOException {

    for (int i = 0; i < sites.size(); i++) {

      Iterable<ECSiteAccountDAO> accountDAOS = ecSiteAccountRepository.findAllByEcSite(sites.get(i));
      for (ECSiteAccountDAO ecSiteAccountDAO : accountDAOS) {

        if (ecSiteAccountDAO.getEcUseFlag() != Boolean.TRUE) {
          LOGGER.info("EC Site [" + ecSiteAccountDAO.getId() + ":" + ecSiteAccountDAO.getEcSite()
              + "] is not active. Skipped.");
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
          GeneralPurchaseHistoryCrawler crawler = new GeneralPurchaseHistoryCrawler(sites.get(i), webpageService);

          crawler.setScript(this.script);

          GeneralPurchaseHistoryCrawlerResult crawlerResult = crawler.fetchPurchaseHistoryList(webClient,
              lastPurchaseHistory.orElse(null), true);
          webClient.finishTraffic();

          this.list = crawlerResult.getPurchaseHistoryList();

          if (list != null && list.size() > 0) {
            final String accountId = "" + ecSiteAccountDAO.getId();
            list.forEach(purchaseHistory -> {
              purchaseHistory.setAccountId(accountId);
              LOGGER.info(String.format("purchaseHistory#%s accountid: %s", purchaseHistory.getOrderNumber(),
                  purchaseHistory.getAccountId()));
            });

            //purchaseHistoryService.save(ecSiteAccountDAO.getEcSite(), list);
          }
          LOGGER.info("succeed fetch purchaseHistory for ecSite id = " + ecSiteAccountDAO.getId());
        } catch (Exception e) { // here catch all exception and did not throw it
          // TODO: arrange login handler
          //this.loginHandler.saveFailedResult(ecSiteAccountDAO, e.getMessage());
          LOGGER.error("failed to PurchaseHistory for ecSite id = " + ecSiteAccountDAO.getId());
          e.printStackTrace();
        }
      }
    }
  }

  public List<PurchaseHistory> getPurchaseHistoryList() {
	  return list;
  }

}
