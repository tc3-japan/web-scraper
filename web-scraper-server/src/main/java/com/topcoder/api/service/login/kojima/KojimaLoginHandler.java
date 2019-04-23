package com.topcoder.api.service.login.kojima;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.util.Cookie;
import com.topcoder.api.exception.ApiException;
import com.topcoder.api.service.login.LoginHandler;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.AuthStatusType;
import com.topcoder.common.model.ECCookie;
import com.topcoder.common.model.ECCookies;
import com.topcoder.common.model.LoginRequest;
import com.topcoder.common.model.LoginResponse;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.AuthStep;
import com.topcoder.scraper.module.kojima.crawler.KojimaAuthenticationCrawler;
import com.topcoder.scraper.service.WebpageService;

@Component
public class KojimaLoginHandler implements LoginHandler {

  /**
   * ec site account repository
   */
  @Autowired
  ECSiteAccountRepository ecSiteAccountRepository;

  @Autowired
  private ApplicationContext applicationContext;

  
  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaLoginHandler.class);

  @Override
  public String getECSite() {
    return "kojima";
  }

  @Override
  public LoginResponse loginInit(int userId, Integer siteId, String uuid) throws ApiException {
    return new LoginResponse(null, null, null, AuthStep.SECOND, null);
  }

  @Override
  public LoginResponse login(int userId, LoginRequest request) throws ApiException {
    
    ECSiteAccountDAO ecSiteAccountDAO = ecSiteAccountRepository.findOne(request.getSiteId());
    ecSiteAccountDAO.setPassword(request.getPassword());
    ecSiteAccountDAO.setLoginEmail(request.getEmail());
    ecSiteAccountRepository.save(ecSiteAccountDAO); // save it first

    KojimaAuthenticationCrawler crawler = new KojimaAuthenticationCrawler("kojima", applicationContext.getBean(WebpageService.class));
    TrafficWebClient webClient = new TrafficWebClient(userId, false);
    
    try {
      boolean result = crawler.authenticate(webClient, request.getEmail(), request.getPassword());
      if (result) { // succeed , update status and save cookies

        ecSiteAccountDAO.setAuthStatus(AuthStatusType.SUCCESS);
        ecSiteAccountDAO.setAuthFailReason(null);
        List<ECCookie> ecCookies = new LinkedList<>();
        for (Cookie cookie : webClient.getWebClient().getCookieManager().getCookies()) {
          ECCookie ecCookie = new ECCookie();
          ecCookie.setName(cookie.getName());
          ecCookie.setDomain(cookie.getDomain());
          ecCookie.setValue(cookie.getValue());
          ecCookie.setExpires(cookie.getExpires());
          ecCookie.setHttpOnly(cookie.isHttpOnly());
          ecCookie.setPath(cookie.getPath());
          ecCookie.setSecure(cookie.isSecure());
          ecCookies.add(ecCookie);
        }
        ecSiteAccountDAO.setEcCookies(new ECCookies(ecCookies).toJSONString());
        ecSiteAccountRepository.save(ecSiteAccountDAO);

        return new LoginResponse(ecSiteAccountDAO.getLoginEmail(), null, null, AuthStep.DONE, "");
      } else { // login failed
        ecSiteAccountDAO.setAuthStatus(AuthStatusType.FAILED);
        ecSiteAccountDAO.setAuthFailReason("REASON");
        ecSiteAccountRepository.save(ecSiteAccountDAO);
        throw new ApiException("REASON");
      }
    } catch (IOException e) {
      throw new ApiException(e.getMessage());
    }
  }

}
