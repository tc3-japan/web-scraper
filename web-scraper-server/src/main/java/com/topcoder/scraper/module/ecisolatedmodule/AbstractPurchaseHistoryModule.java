package com.topcoder.scraper.module.ecisolatedmodule;

import com.topcoder.api.service.login.LoginHandlerBase;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.Common;
import com.topcoder.scraper.module.IPurchaseHistoryModule;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractPurchaseHistoryCrawler;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractPurchaseHistoryCrawlerResult;
import com.topcoder.scraper.service.PurchaseHistoryService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Abstract class of PurchaseHistoryModule
 */
public abstract class AbstractPurchaseHistoryModule implements IPurchaseHistoryModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPurchaseHistoryModule.class);

    protected final PurchaseHistoryService purchaseHistoryService;
    protected final WebpageService webpageService;
    protected final ECSiteAccountRepository ecSiteAccountRepository;
    protected final LoginHandlerBase loginHandler;
    protected final AbstractPurchaseHistoryCrawler crawler;

    public AbstractPurchaseHistoryModule(
            PurchaseHistoryService purchaseHistoryService,
            ECSiteAccountRepository ecSiteAccountRepository,
            WebpageService webpageService,
            LoginHandlerBase loginHandler,
            AbstractPurchaseHistoryCrawler crawler
    ) {
        this.purchaseHistoryService = purchaseHistoryService;
        this.webpageService = webpageService;
        this.ecSiteAccountRepository = ecSiteAccountRepository;
        this.loginHandler = loginHandler;
        this.crawler = crawler;
    }

    @Override
    public abstract String getModuleType();

    /**
     * Implementation of fetchPurchaseHistoryList method
     */
    @Override
    public void fetchPurchaseHistoryList(List<String> sites) {

        Iterable<ECSiteAccountDAO> accountDAOS = ecSiteAccountRepository.findAllByEcSite(getModuleType());

        for (ECSiteAccountDAO ecSiteAccountDAO : accountDAOS) {
            Optional<PurchaseHistory> lastPurchaseHistory = purchaseHistoryService.fetchLast(ecSiteAccountDAO.getId());

            AbstractPurchaseHistoryCrawlerResult crawlerResult =
                    this.fetchPurchaseHistoryListForECSiteAccount(ecSiteAccountDAO, lastPurchaseHistory.orElse(null));

            if (crawlerResult != null) {
                List<PurchaseHistory> list = crawlerResult.getPurchaseHistoryList();
                if (list != null && list.size() > 0) {
                    list.forEach(purchaseHistory -> purchaseHistory.setAccountId(Integer.toString(ecSiteAccountDAO.getId())));
                    purchaseHistoryService.save(getModuleType(), list);
                }
            }
        }
    }

    public AbstractPurchaseHistoryCrawlerResult fetchPurchaseHistoryListForECSiteAccount(ECSiteAccountDAO ecSiteAccountDAO, PurchaseHistory lastPurchaseHistory) {
        if (ecSiteAccountDAO.getEcUseFlag() != Boolean.TRUE) {
            LOGGER.info("EC Site [" + ecSiteAccountDAO.getId() + ":" + ecSiteAccountDAO.getEcSite() + "] is not active. Skipped.");
            return null;
        }

        TrafficWebClient webClient = new TrafficWebClient(ecSiteAccountDAO.getUserId(), true);
        LOGGER.info("web client version = " + webClient.getWebClient().getBrowserVersion());
        boolean restoreRet = Common.restoreCookies(webClient.getWebClient(), ecSiteAccountDAO);
        if (!restoreRet) {
            LOGGER.error("skip ec site account id = " + ecSiteAccountDAO.getId() + ", restore cookies failed");
            return null;
        }

        try {
            AbstractPurchaseHistoryCrawlerResult crawlerResult = this.crawler.fetchPurchaseHistoryList(webClient, lastPurchaseHistory, true);
            webClient.finishTraffic();
            LOGGER.info("succeed fetch purchaseHistory for ec site account id = " + ecSiteAccountDAO.getId());
            return crawlerResult;

        } catch (Exception e) { // here catch all exception and did not throw it
            loginHandler.saveFailedResult(ecSiteAccountDAO, e.getMessage());
            LOGGER.error("failed to PurchaseHistory for ec site account id = " + ecSiteAccountDAO.getId());
            e.printStackTrace();
        }
        return null;
    }
}
