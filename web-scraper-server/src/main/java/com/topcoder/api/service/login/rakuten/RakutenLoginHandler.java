package com.topcoder.api.service.login.rakuten;

import com.topcoder.api.exception.ApiException;
import com.topcoder.api.service.login.LoginHandlerBase;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.LoginRequest;
import com.topcoder.common.model.LoginResponse;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.UserRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ecisolatedmodule.rakuten.crawler.RakutenAuthenticationCrawler;
import com.topcoder.scraper.module.ecisolatedmodule.rakuten.crawler.RakutenAuthenticationCrawlerResult;
import com.topcoder.scraper.module.ecunifiedmodule.AuthStep;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

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
    ecSiteAccountDAO.setPassword(request.getPassword());
    ecSiteAccountDAO.setLoginEmail(request.getEmail());
    ecSiteAccountRepository.save(ecSiteAccountDAO); // save it first

    RakutenAuthenticationCrawler crawler = new RakutenAuthenticationCrawler(this.getECSite(), applicationContext.getBean(WebpageService.class));
    TrafficWebClient webClient = new TrafficWebClient(userId, false);
    
    try {
      RakutenAuthenticationCrawlerResult result = crawler.authenticate(webClient, request.getEmail(), request.getPassword(), null);
      if (result.isSuccess()) { // succeed , update status and save cookies
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutput oout = new ObjectOutputStream(bout);
        oout.writeObject(webClient.getWebClient().getCookieManager().getCookies());
        oout.close();
        bout.close();
        ecSiteAccountDAO.setEcCookies(bout.toByteArray());

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
