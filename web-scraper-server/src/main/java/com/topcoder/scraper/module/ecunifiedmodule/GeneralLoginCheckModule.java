package com.topcoder.scraper.module.ecunifiedmodule;

import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.dao.UserDAO;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.UserRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.Common;
import com.topcoder.scraper.exception.CheckLoginException;
import com.topcoder.scraper.module.ILoginCheckModule;

import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralPurchaseHistoryCrawler;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

/**
 * General implementation of LoginCheckModule
 */
@Component
public class GeneralLoginCheckModule implements ILoginCheckModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralLoginCheckModule.class);

    private final WebpageService webpageService;
    private final ECSiteAccountRepository ecSiteAccountRepository;
    private final UserRepository userRepository;
    private GeneralPurchaseHistoryCrawler crawler;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    public GeneralLoginCheckModule(ECSiteAccountRepository ecSiteAccountRepository, WebpageService webpageService, UserRepository userRepository) {
        this.ecSiteAccountRepository = ecSiteAccountRepository;
        this.webpageService = webpageService;
        this.userRepository = userRepository;
    }

    @Override
    public String getModuleType() { return "general"; }

    @Override
    public void checkLogin() {
        LOGGER.debug("[checkLogin] in");

        Iterable<UserDAO> userDAOS = userRepository.findAll();
        for (UserDAO userDAO : userDAOS) {
            Iterable<ECSiteAccountDAO> accountDAOS = ecSiteAccountRepository.findAllByUserIdAndIsLogin(userDAO.getId(), Boolean.TRUE);
            if (accountDAOS == null) { continue; }

            for (ECSiteAccountDAO ecSiteAccountDAO : accountDAOS) {
                LoginCheckResult loginCheckResult = this.goToPurchaseHistoryListForECSiteAccount(ecSiteAccountDAO);

                if (loginCheckResult.equals(LoginCheckResult.SKIPPED)) {
                    continue;
                } else if (loginCheckResult.equals(LoginCheckResult.SUCCESS)) {
                    ecSiteAccountDAO.setIsLogin(true);
                    ecSiteAccountDAO.setLastLoginedAt(Date.from(Instant.now()));
                    ecSiteAccountDAO.setUpdateAt(Date.from(Instant.now()));
                } else if (loginCheckResult.equals(LoginCheckResult.FAILED)) {
                    ecSiteAccountDAO.setIsLogin(false);
                    ecSiteAccountDAO.setUpdateAt(Date.from(Instant.now()));
                }
                ecSiteAccountRepository.save(ecSiteAccountDAO);
            }
        }
    }

    private LoginCheckResult goToPurchaseHistoryListForECSiteAccount(ECSiteAccountDAO ecSiteAccountDAO) {
        if (ecSiteAccountDAO.getEcUseFlag() != Boolean.TRUE) {
            LOGGER.info("EC Site [" + ecSiteAccountDAO.getId() + ":" + ecSiteAccountDAO.getEcSite() + "] is not active. Skipped.");
            return LoginCheckResult.SKIPPED;
        }
        this.crawler = new GeneralPurchaseHistoryCrawler(ecSiteAccountDAO.getEcSite(), webpageService, this.configurationRepository);

        TrafficWebClient webClient = new TrafficWebClient(ecSiteAccountDAO.getUserId(), true);
        boolean restoreRet = Common.restoreCookies(webClient.getWebClient(), ecSiteAccountDAO);
        if (!restoreRet) {
            LOGGER.error("Skip ec site account id = " + ecSiteAccountDAO.getId() + ", restore cookies failed.");
            return LoginCheckResult.FAILED;
        }

        try {
            this.crawler.goToPurchaseHistoryListForLoginCheck(webClient);
            webClient.finishTraffic();
            return LoginCheckResult.SUCCESS;

        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error due to IOException. Failed to login for ec site account id = " + ecSiteAccountDAO.getId());
            return LoginCheckResult.FAILED;
        } catch (CheckLoginException e) {
            LOGGER.error(e.getMessage());
            return LoginCheckResult.FAILED;
        }
    }

    private enum LoginCheckResult {
        SKIPPED,
        SUCCESS,
        FAILED,
    }
}
