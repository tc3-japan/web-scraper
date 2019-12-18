package com.topcoder.scraper.module.ecisolatedmodule.rakuten.crawler;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.lib.navpage.NavigableAuthenticationPage;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractAuthenticationCrawler;
import com.topcoder.scraper.service.WebpageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Rakuten implementation of AuthenticationCrawler
 */
@Component
public class RakutenAuthenticationCrawler extends AbstractAuthenticationCrawler {

  private Logger logger = LoggerFactory.getLogger(RakutenAuthenticationCrawler.class.getName());
  
  private String siteName;
  
  private final WebpageService webpageService;
  
  public RakutenAuthenticationCrawler(WebpageService webpageService) {
    this.siteName = "rakuten";
    this.webpageService = webpageService;
  }

  // TODO : implement
  @Override
  public RakutenAuthenticationCrawlerResult authenticate(TrafficWebClient webClient,
                                                       String username,
                                                       String password, String code, boolean init) throws IOException {
    return new RakutenAuthenticationCrawlerResult(false, null);
  }

  @Override
  public RakutenAuthenticationCrawlerResult authenticate(TrafficWebClient webClient, String username, String password) throws IOException {
    webClient.getWebClient().getCookieManager().clearCookies();
    webClient.getWebClient().getOptions().setJavaScriptEnabled(true);
    HtmlPage page = webClient.getPage("https://grp01.id.rakuten.co.jp/rms/nid/vc?__event=login&service_id=top");
    NavigableAuthenticationPage authPage = new NavigableAuthenticationPage(page, webClient);
    
    authPage.type(username, "#loginInner_u");
    authPage.savePage("1-rakuten-auth", "rakuten", webpageService);
    authPage.typePassword(password, "#loginInner_p"); //or #code
    authPage.typeCheckbox("off", "#auto_logout");
    authPage.click(".loginButton", webpageService);
    authPage.savePage("5-rakuten-auth", "rakuten", webpageService);

    authPage.savePage("rakuten-authenticated", siteName, webpageService);

    authPage.confirmLoginByElementExists(".header-logo");
    
    return new RakutenAuthenticationCrawlerResult(authPage.getLoginStatus(), null);
  }
}
