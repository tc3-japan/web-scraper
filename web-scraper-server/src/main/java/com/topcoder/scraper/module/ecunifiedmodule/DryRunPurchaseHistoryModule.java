package com.topcoder.scraper.module.ecunifiedmodule;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.traffic.TrafficWebClient.TrafficWebClientForDryRun;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(DryRunPurchaseHistoryModule.class);

  private final PurchaseHistoryService purchaseHistoryService;
  private final WebpageService webpageService;
  private final ECSiteAccountRepository ecSiteAccountRepository;

  private String config;
  private List<PurchaseHistory> purchaseHistoryList;
  private List<String> htmlPathList;

  @Autowired
  ConfigurationRepository configurationRepository;

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

  public void setConfig(String config) {
    this.config = config;
  }

  @Override
  public String getModuleType() {
    return "dryrun";
  }

  @Override
  public void fetchPurchaseHistoryList(List<String> sites) throws IOException {

    // reset list
    this.purchaseHistoryList = null;
    this.htmlPathList = null;

    ECSiteAccountDAO accountDAO = null;
    String site = sites.get(0);

    Iterable<ECSiteAccountDAO> accountDAOS = ecSiteAccountRepository.findAllByEcSite(site);
    for (ECSiteAccountDAO ecSiteAccountDAO : accountDAOS) {
      if (ecSiteAccountDAO.getEcUseFlag() == Boolean.TRUE) {
        accountDAO = ecSiteAccountDAO;
        break;
      }
    }

    if (accountDAO == null) {
      LOGGER.error("failed to get ecSite account");
      return;
    }

    TrafficWebClient webClient = new TrafficWebClient(accountDAO.getUserId(), true);
    TrafficWebClientForDryRun webClientForDryRun = webClient.new TrafficWebClientForDryRun(accountDAO.getUserId(), true);
    LOGGER.info("web client version = " + webClient.getWebClient().getBrowserVersion());
    boolean restoreRet = Common.restoreCookies(webClientForDryRun.getWebClient(), accountDAO);
    if (!restoreRet) {
      LOGGER.error("skip ecSite id = " + accountDAO.getId() + ", restore cookies failed");
      return;
    }

    try {
      GeneralPurchaseHistoryCrawler crawler = new GeneralPurchaseHistoryCrawler(site, this.webpageService, this.configurationRepository);
      crawler.setConfig(this.config);
      GeneralPurchaseHistoryCrawlerResult crawlerResult = crawler.fetchPurchaseHistoryList(webClientForDryRun, true);
      this.purchaseHistoryList = crawlerResult.getPurchaseHistoryList();
      this.htmlPathList = crawlerResult.getHtmlPathList();
      LOGGER.info("succeed fetch purchaseHistory for ecSite id = " + accountDAO.getId());
    } catch (Exception e) {
      LOGGER.error("failed to PurchaseHistory for ecSite id = " + accountDAO.getId());
      e.printStackTrace();
    }

  }

  public List<PurchaseHistory> getPurchaseHistoryList() {
	  return this.purchaseHistoryList;
  }

  public List<String> getHtmlPathList() {
    // change path from abstract to relative that start at [logs] folder
    for (int i = 0; i < htmlPathList.size(); i++) {
      String htmlPath = htmlPathList.get(i);
      int delimiterIntex = htmlPath.lastIndexOf("logs");
      // number 4 means "logs" charactor count
      htmlPathList.set(i, "html" + htmlPath.substring(delimiterIntex + 4, htmlPath.length()));
    }
    return this.htmlPathList;
  }

}
