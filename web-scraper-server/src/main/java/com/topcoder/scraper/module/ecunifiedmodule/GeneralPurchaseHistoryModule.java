package com.topcoder.scraper.module.ecunifiedmodule;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.topcoder.scraper.exception.NotLoggedinException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.PurchaseHistoryRepository;
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
public class GeneralPurchaseHistoryModule implements IPurchaseHistoryModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPurchaseHistoryModule.class);

    private final PurchaseHistoryService purchaseHistoryService;
    private final WebpageService webpageService;
    private final ECSiteAccountRepository ecSiteAccountRepository;
    private GeneralPurchaseHistoryCrawler crawler;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    PurchaseHistoryRepository historyRepository;

    // TODO: arrange login handler
    //private final LoginHandlerBase loginHandler;

    @Autowired
    public GeneralPurchaseHistoryModule(PurchaseHistoryService purchaseHistoryService, ECSiteAccountRepository ecSiteAccountRepository, WebpageService webpageService
                                        //LoginHandlerBase loginHandler
    ) {
        this.purchaseHistoryService = purchaseHistoryService;
        this.webpageService = webpageService;
        this.ecSiteAccountRepository = ecSiteAccountRepository;
        // TODO: arrange login handler
        //this.loginHandler = loginHandler;
    }

    @Override
    public String getModuleType() {
        return "general";
    }

    @Override
    public void fetchPurchaseHistoryList(List<String> sites) throws IOException {

        for (String site : sites) {
            Iterable<ECSiteAccountDAO> accountDAOS = ecSiteAccountRepository.findAllByEcSite(site);

            for (ECSiteAccountDAO ecSiteAccountDAO : accountDAOS) {
                if (ecSiteAccountDAO.getIsLogin() != null && !ecSiteAccountDAO.getIsLogin()) {
                    LOGGER.info("Not logged in EC Site [" + ecSiteAccountDAO.getId() + ":" + ecSiteAccountDAO.getEcSite() + "], Skipped.");
                    continue;
                }

                // TODO: "lastPurchaseHistory" is no used.
                Optional<PurchaseHistory> lastPurchaseHistory = purchaseHistoryService.fetchLast(ecSiteAccountDAO.getId());

                GeneralPurchaseHistoryCrawlerResult crawlerResult =
                        this.fetchPurchaseHistoryListForECSiteAccount(ecSiteAccountDAO, lastPurchaseHistory.orElse(null), -1);

                if (crawlerResult != null) {
                    List<PurchaseHistory> list = crawlerResult.getPurchaseHistoryList();
                    if (list != null && list.size() > 0) {
                        list.forEach(purchaseHistory -> purchaseHistory.setAccountId(Integer.toString(ecSiteAccountDAO.getId())));
                        purchaseHistoryService.save(site, list);
                    }
                }
            }
        }
    }

    public GeneralPurchaseHistoryCrawlerResult fetchPurchaseHistoryListForECSiteAccount(ECSiteAccountDAO ecSiteAccountDAO, PurchaseHistory lastPurchaseHistory, int maxCount) {
        if (ecSiteAccountDAO.getEcUseFlag() != Boolean.TRUE) {
            LOGGER.info("EC Site [" + ecSiteAccountDAO.getId() + ":" + ecSiteAccountDAO.getEcSite() + "] is not active. Skipped.");
            return null;
        }
        this.crawler = new GeneralPurchaseHistoryCrawler(ecSiteAccountDAO.getEcSite(), webpageService, this.configurationRepository, this.historyRepository, maxCount);

        TrafficWebClient webClient = new TrafficWebClient(ecSiteAccountDAO.getUserId(), true);
        LOGGER.info("web client version = " + webClient.getWebClient().getBrowserVersion());
        boolean restoreRet = Common.restoreCookies(webClient.getWebClient(), ecSiteAccountDAO);
        if (!restoreRet) {
            LOGGER.error("skip ec site account id = " + ecSiteAccountDAO.getId() + ", restore cookies failed");
            return null;
        }

        try {
            GeneralPurchaseHistoryCrawlerResult crawlerResult = this.crawler.fetchPurchaseHistoryList(webClient);
            webClient.finishTraffic();
            LOGGER.info("succeed fetch purchaseHistory for ec site account id = " + ecSiteAccountDAO.getId());
            return crawlerResult;

        } catch (IOException e) {
            // TODO: arrange login handler
            //this.loginHandler.saveFailedResult(ecSiteAccountDAO, e.getMessage());
            LOGGER.error("failed to PurchaseHistory for ec site account id = " + ecSiteAccountDAO.getId());
            e.printStackTrace();
        } catch (NotLoggedinException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

}
