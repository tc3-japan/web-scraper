package com.topcoder.api.service.login.rakuten;

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
import com.topcoder.api.service.login.LoginHandlerBase;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.ECCookie;
import com.topcoder.common.model.ECCookies;
import com.topcoder.common.model.LoginRequest;
import com.topcoder.common.model.LoginResponse;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.UserRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ecunifiedmodule.AuthStep;
import com.topcoder.scraper.module.ecisolatedmodule.rakuten.crawler.RakutenAuthenticationCrawler;
import com.topcoder.scraper.service.WebpageService;

@Component
public class RakutenLoginHandler extends LoginHandlerBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(RakutenLoginHandler.class);

  private ApplicationContext applicationContext;

  @Autowired
  public RakutenLoginHandler(ECSiteAccountRepository ecSiteAccountRepository,
      UserRepository userRepository, ApplicationContext applicationContext) {
    super(ecSiteAccountRepository, userRepository);
    this.applicationContext = applicationContext;
  }
  
  @Override
  public String getECSite() {
    return "rakuten";
  }

  @Override
  public LoginResponse loginInit(int userId, Integer siteId, String uuid) throws ApiException {
    return new LoginResponse(null, null, null, AuthStep.SECOND, null);
  }

  @Override
  public LoginResponse login(int userId, LoginRequest request) throws ApiException {
    
    ECSiteAccountDAO ecSiteAccountDAO = ecSiteAccountRepository.findOne(request.getSiteId());
    //TODO.property: Use application.yaml instead -- or don't use this at all. It's just for testing
    ecSiteAccountDAO.setPassword(request.getPassword());
    ecSiteAccountDAO.setLoginEmail(request.getEmail());

    System.out.println("\n\n\n>>>>>> Logging into Rakuten with account " + ecSiteAccountDAO.getLoginEmail() + "\n\n\n");
    ecSiteAccountRepository.save(ecSiteAccountDAO); // save it first

    RakutenAuthenticationCrawler crawler = new RakutenAuthenticationCrawler(applicationContext.getBean(WebpageService.class));
    TrafficWebClient webClient = new TrafficWebClient(userId, false);
    
    try {
      boolean result = crawler.authenticate(webClient, request.getEmail(), request.getPassword()).isSuccess();
      if (result) { // succeed , update status and save cookies
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
        saveSuccessResult(ecSiteAccountDAO);

        return new LoginResponse(ecSiteAccountDAO.getLoginEmail(), null, null, AuthStep.DONE, "");
      } else { // login failed
        saveFailedResult(ecSiteAccountDAO, "Authentication failed");
        throw new ApiException("Authentication failed");
      }
    } catch (IOException e) {
      saveFailedResult(ecSiteAccountDAO, e.getMessage());
      throw new ApiException(e.getMessage());
    }
  }

}
