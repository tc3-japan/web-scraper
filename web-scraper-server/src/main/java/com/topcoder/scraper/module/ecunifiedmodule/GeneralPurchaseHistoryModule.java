package com.topcoder.scraper.module.ecunifiedmodule;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.topcoder.api.service.login.LoginHandler;
import com.topcoder.api.service.login.LoginHandlerFactory;
import com.topcoder.common.model.AuthStatusType;
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

    @Autowired
    private LoginHandlerFactory loginHandlerFactory;

    @Autowired
    public GeneralPurchaseHistoryModule(PurchaseHistoryService purchaseHistoryService, ECSiteAccountRepository ecSiteAccountRepository, WebpageService webpageService
    ) {
        this.purchaseHistoryService = purchaseHistoryService;
        this.webpageService = webpageService;
        this.ecSiteAccountRepository = ecSiteAccountRepository;
        // TODO: arrange login handler
    }

    @Override
    public String getModuleType() {
        return "general";
    }

    @Override
    public void fetchPurchaseHistoryList(List<String> sites) {

        for (String site : sites) {
            Iterable<ECSiteAccountDAO> accountDAOS = ecSiteAccountRepository.findAllByEcSite(site);

            for (ECSiteAccountDAO ecSiteAccountDAO : accountDAOS) {
                if (ecSiteAccountDAO.getIsLogin() == null) {
                    LOGGER.info("Not logged in EC Site [" + ecSiteAccountDAO.getId() + ":" + ecSiteAccountDAO.getEcSite() + "], Skipped.");
                    continue;
                }

                GeneralPurchaseHistoryCrawlerResult crawlerResult =
                        this.fetchPurchaseHistoryListForECSiteAccount(ecSiteAccountDAO, null);

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

    public GeneralPurchaseHistoryCrawlerResult fetchPurchaseHistoryListForECSiteAccount(ECSiteAccountDAO ecSiteAccountDAO, Integer maxCountForDetection) {
        if (ecSiteAccountDAO.getEcUseFlag() != Boolean.TRUE) {
            LOGGER.info("EC Site [" + ecSiteAccountDAO.getId() + ":" + ecSiteAccountDAO.getEcSite() + "] is not active. Skipped.");
            return null;
        }

        LoginHandler loginHandler = loginHandlerFactory.getLoginHandler(ecSiteAccountDAO.getEcSite());
        this.crawler = new GeneralPurchaseHistoryCrawler
                (ecSiteAccountDAO.getEcSite(), webpageService,
                        this.configurationRepository, this.historyRepository,
                        loginHandler, ecSiteAccountDAO, maxCountForDetection);

        TrafficWebClient webClient = new TrafficWebClient(ecSiteAccountDAO.getUserId(), true);
        LOGGER.info("web client version = " + webClient.getWebClient().getBrowserVersion());
        boolean restoreRet = Common.restoreCookies(webClient.getWebClient(), ecSiteAccountDAO);
        if (!restoreRet) {
            String message = "skip ec site account id = " + ecSiteAccountDAO.getId() + ", restore cookies failed";
            Common.ZabbixLog(LOGGER, message);
            return null;
        }

        try {
            GeneralPurchaseHistoryCrawlerResult crawlerResult = this.crawler.fetchPurchaseHistoryList(webClient);
            webClient.finishTraffic();
            LOGGER.info("succeed fetch purchaseHistory for ec site account id = " + ecSiteAccountDAO.getId());
            return crawlerResult;

        } catch (IOException e) {
            String message = "failed to PurchaseHistory for ec site account id = " + ecSiteAccountDAO.getId();
            Common.ZabbixLog(LOGGER, message, e);
        } catch (NotLoggedinException e) {
            Common.ZabbixLog(LOGGER, e);

            ecSiteAccountDAO.setAuthStatus(AuthStatusType.LOGGED_OUT);
            ecSiteAccountDAO.setIsLogin(false);
            ecSiteAccountDAO.setUpdateAt(Date.from(Instant.now()));
            ecSiteAccountRepository.save(ecSiteAccountDAO);
        }
        return null;
    }

}
