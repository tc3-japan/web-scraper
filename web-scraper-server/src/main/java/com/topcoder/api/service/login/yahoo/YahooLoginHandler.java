package com.topcoder.api.service.login.yahoo;

import com.gargoylesoftware.htmlunit.util.Cookie;
import com.topcoder.api.exception.ApiException;
import com.topcoder.api.exception.BadRequestException;
import com.topcoder.api.service.login.LoginHandlerBase;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.CodeType;
import com.topcoder.common.model.CrawlerContext;
import com.topcoder.common.model.ECCookie;
import com.topcoder.common.model.ECCookies;
import com.topcoder.common.model.LoginRequest;
import com.topcoder.common.model.LoginResponse;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.UserRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler.YahooAuthenticationCrawler;
import com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler.YahooAuthenticationCrawlerResult;
import com.topcoder.scraper.module.ecunifiedmodule.AuthStep;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class YahooLoginHandler extends LoginHandlerBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooLoginHandler.class);

  private ApplicationContext applicationContext;

  /**
   * save Crawler context
   */
  private Map<Integer, CrawlerContext> crawlerContextMap = new HashMap<>();

  @Autowired
  public YahooLoginHandler(ECSiteAccountRepository ecSiteAccountRepository,
      UserRepository userRepository, ApplicationContext applicationContext) {
    super(ecSiteAccountRepository, userRepository);
    this.applicationContext = applicationContext;
  }
  
  @Override
  public String getECSite() {
    return "yahoo";
  }

  @Override
  public LoginResponse loginInit(int userId, Integer siteId, String uuid) throws ApiException {

    CrawlerContext context = crawlerContextMap.get(siteId);

    if (context == null || !context.getUuid().equals(uuid)) { // ignore previous context, because of uuid is different
      context = new CrawlerContext(new TrafficWebClient(userId, false),
              null,
              applicationContext.getBean(WebpageService.class),
              uuid, null);
      context.setCrawler(new YahooAuthenticationCrawler(context.getWebpageService()));

      crawlerContextMap.put(siteId, context); // save context
    }

    return new LoginResponse(null, null, null, AuthStep.FIRST, null);
  }

  @Override
  public LoginResponse login(int userId, LoginRequest request) throws ApiException {
    
    ECSiteAccountDAO ecSiteAccountDAO = ecSiteAccountRepository.findOne(request.getSiteId());

    ecSiteAccountDAO.setPassword(request.getPassword());
    ecSiteAccountDAO.setLoginEmail(request.getEmail());
    ecSiteAccountRepository.save(ecSiteAccountDAO); // save it first

    CrawlerContext context = crawlerContextMap.get(request.getSiteId());
    if (context == null || !context.getUuid().equals(request.getUuid())) { // context error
      saveFailedResult(ecSiteAccountDAO, "crawler context error");
      throw new BadRequestException(ecSiteAccountDAO.getAuthFailReason());
    }

    try {
      YahooAuthenticationCrawler crawler = (YahooAuthenticationCrawler)context.getCrawler();
      YahooAuthenticationCrawlerResult result = crawler.authenticate(
              context.getWebClient(), request.getEmail(), request.getPassword(), request.getCode());

      if (result.isSuccess()) { // succeed , update status and save cookies
        List<ECCookie> ecCookies = new LinkedList<>();
        for (Cookie cookie : context.getWebClient().getWebClient().getCookieManager().getCookies()) {
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

        return new LoginResponse(ecSiteAccountDAO.getLoginEmail(), null, null,
                AuthStep.DONE, "");
      } else { // login failed

        // VerifyCodeLogin
        if (result.getCodeType() == CodeType.VerifyCodeLogin) {
          return new LoginResponse(ecSiteAccountDAO.getLoginEmail(), result.getCodeType(), null,
                  AuthStep.SECOND, "");
        }

        saveFailedResult(ecSiteAccountDAO, "Authentication failed");
        throw new ApiException("Authentication failed");
      }
    } catch (IOException e) {
      saveFailedResult(ecSiteAccountDAO, e.getMessage());
      throw new ApiException(e.getMessage());
    }
  }

}
