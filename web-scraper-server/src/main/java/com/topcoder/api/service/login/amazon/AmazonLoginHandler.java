package com.topcoder.api.service.login.amazon;

import com.topcoder.api.exception.ApiException;
import com.topcoder.api.exception.BadRequestException;
import com.topcoder.api.service.login.LoginHandlerBase;
import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.CrawlerContext;
import com.topcoder.common.model.LoginRequest;
import com.topcoder.common.model.LoginResponse;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.UserRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler.AmazonAuthenticationCrawler;
import com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler.AmazonAuthenticationCrawlerResult;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class AmazonLoginHandler extends LoginHandlerBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonLoginHandler.class);

  private final AmazonProperty amazonProperty;

  private final ApplicationContext applicationContext;

  /**
   * save Crawler context
   */
  private Map<Integer, CrawlerContext> crawlerContextMap = new HashMap<>();

  @Autowired
  public AmazonLoginHandler(ECSiteAccountRepository ecSiteAccountRepository,
      UserRepository userRepository, AmazonProperty amazonProperty, ApplicationContext applicationContext) {
    super(ecSiteAccountRepository, userRepository);
    this.amazonProperty = amazonProperty;
    this.applicationContext = applicationContext;
  }
  
  @Override
  public String getECSite() {
    return "amazon";
  }
  
  @Override
  public LoginResponse loginInit(int userId, Integer siteId, String uuid) throws ApiException {
    ECSiteAccountDAO ecSiteAccountDAO = ecSiteAccountRepository.findOne(siteId);

    CrawlerContext context = crawlerContextMap.get(siteId);

    if (context == null || !context.getUuid().equals(uuid)) { // ignore previous context, because of uuid is different
      // TODO: Amazon-specific code
      context = new CrawlerContext(new TrafficWebClient(userId, false),
        amazonProperty,
        applicationContext.getBean(WebpageService.class),
        uuid, null);
      context.setCrawler(new AmazonAuthenticationCrawler(context.getProperty(), context.getWebpageService()));

      crawlerContextMap.put(siteId, context); // save context
    }

    try {
      AmazonAuthenticationCrawler crawler = (AmazonAuthenticationCrawler)context.getCrawler();
      AmazonAuthenticationCrawlerResult result = crawler.authenticate(
              context.getWebClient(),null, null, null, true);
      if (result.isSuccess()) {
        return new LoginResponse(ecSiteAccountDAO.getLoginEmail(), null, null,
          context.getCrawler().getAuthStep(), result.getReason());
      } else {
        saveFailedResult(ecSiteAccountDAO, result.getReason());
        return new LoginResponse(ecSiteAccountDAO.getLoginEmail(), result.getCodeType(), result.getImg(),
          context.getCrawler().getAuthStep(), result.getReason());
      }
    } catch (Exception e) { // here is fatal error, cannot continue
      saveFailedResult(ecSiteAccountDAO, e.getMessage());
      throw new ApiException(e.getMessage());
    }
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
      AmazonAuthenticationCrawler crawler = (AmazonAuthenticationCrawler)context.getCrawler();
      AmazonAuthenticationCrawlerResult result = crawler.authenticate(
              context.getWebClient(), request.getEmail(), request.getPassword(), request.getCode(), false);

      if (result.isSuccess()) { // succeed , update status and save cookies
        /*
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
        */

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutput oout = new ObjectOutputStream(bout);
        oout.writeObject(context.getWebClient().getWebClient().getCookieManager().getCookies());
        oout.close();
        bout.close();
        ecSiteAccountDAO.setEcCookies(bout.toByteArray());
        saveSuccessResult(ecSiteAccountDAO);

        return new LoginResponse(ecSiteAccountDAO.getLoginEmail(), result.getCodeType(), result.getImg(),
          context.getCrawler().getAuthStep(), result.getReason());
      } else { // login failed
        saveFailedResult(ecSiteAccountDAO, result.getReason());
        if (result.isNeedContinue()) {
          return new LoginResponse(ecSiteAccountDAO.getLoginEmail(), result.getCodeType(), result.getImg(),
            context.getCrawler().getAuthStep(), result.getReason());
        } else { // cannot continue, throw error
          throw new ApiException(result.getReason());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      saveFailedResult(ecSiteAccountDAO, e.getMessage());
      throw new ApiException(e.getMessage());
    }
  }
}
