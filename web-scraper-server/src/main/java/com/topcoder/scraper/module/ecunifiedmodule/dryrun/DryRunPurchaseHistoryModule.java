package com.topcoder.scraper.module.ecunifiedmodule.dryrun;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.traffic.TrafficWebClient.TrafficWebClientForDryRun;
import com.topcoder.common.util.Common;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralPurchaseHistoryCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralPurchaseHistoryCrawlerResult;
import com.topcoder.scraper.service.WebpageService;

/**
 * Dry run of ProductModule
 */
@Component
public class DryRunPurchaseHistoryModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(DryRunPurchaseHistoryModule.class);

    private final WebpageService webpageService;
    private final ECSiteAccountRepository ecSiteAccountRepository;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    public DryRunPurchaseHistoryModule(ECSiteAccountRepository ecSiteAccountRepository, WebpageService webpageService) {
        this.webpageService = webpageService;
        this.ecSiteAccountRepository = ecSiteAccountRepository;
    }

    public List<Object> fetchPurchaseHistoryList(String site, String config, Integer count) throws IOException {

        ECSiteAccountDAO accountDAO = null;

        Iterable<ECSiteAccountDAO> accountDAOS = ecSiteAccountRepository.findAllByEcSite(site);
        for (ECSiteAccountDAO ecSiteAccountDAO : accountDAOS) {
            if (ecSiteAccountDAO.getEcUseFlag() == Boolean.TRUE) {
                accountDAO = ecSiteAccountDAO;
                break;
            }
        }

        if (accountDAO == null) {
            Common.ZabbixLog(LOGGER, "failed to get ecSite account");
            return null;
        }

        TrafficWebClient webClient = new TrafficWebClient(accountDAO.getUserId(), true);
        TrafficWebClientForDryRun webClientForDryRun = webClient.new TrafficWebClientForDryRun(accountDAO.getUserId(), true);
        LOGGER.info("web client version = " + webClient.getWebClient().getBrowserVersion());
        boolean restoreRet = Common.restoreCookies(webClientForDryRun.getWebClient(), accountDAO);
        if (!restoreRet) {
            String message = "skip ecSite id = " + accountDAO.getId() + ", restore cookies failed";
            Common.ZabbixLog(LOGGER, message);
            return null;
        }

        GeneralPurchaseHistoryCrawler crawler = new GeneralPurchaseHistoryCrawler(site, this.webpageService, this.configurationRepository);
        crawler.setConfig(config);
        DryRunUtils dru = new DryRunUtils(count);
        crawler.setDryRunUtils(dru);
        GeneralPurchaseHistoryCrawlerResult crawlerResult = crawler.fetchPurchaseHistoryList(webClientForDryRun);
        LOGGER.info("succeed fetch purchaseHistory for ecSite id = " + accountDAO.getId());
        return dru.toJsonOfDryRunPurchasehistoryModule(crawlerResult.getPurchaseHistoryList(), crawlerResult.getHtmlPathList());
    }
}
