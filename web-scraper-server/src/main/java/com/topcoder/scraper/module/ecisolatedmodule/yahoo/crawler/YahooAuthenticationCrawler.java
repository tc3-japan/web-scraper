package com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler;

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
 * Yahoo implementation of AuthenticationCrawler
 */
@Component
public class YahooAuthenticationCrawler extends AbstractAuthenticationCrawler {

  private Logger logger = LoggerFactory.getLogger(YahooAuthenticationCrawler.class.getName());
  
  private String siteName;
  
  private final WebpageService webpageService;
  
  public YahooAuthenticationCrawler(WebpageService webpageService) {
    this.siteName = "yahoo";
    this.webpageService = webpageService;
  }

  // TODO : implement
  @Override
  public YahooAuthenticationCrawlerResult authenticate(TrafficWebClient webClient,
                                                       String username,
                                                       String password, String code, boolean init) throws IOException {
    return new YahooAuthenticationCrawlerResult(false, null);
  }

  @Override
  public YahooAuthenticationCrawlerResult authenticate(TrafficWebClient webClient, String username, String password) throws IOException {
    //NOTE: Make sure webClient.getOptions().setJavaScriptEnabled(true); in WebClientConfig.java!
    webClient.getWebClient().getCookieManager().clearCookies();
    webClient.getWebClient().getOptions().setJavaScriptEnabled(true);
    //NavigableAuthenticationPage authPage = new NavigableAuthenticationPage("https://login.yahoo.co.jp/config/login", webClient); //TODO: How to set this parameter?
    HtmlPage page = webClient.getPage("https://login.yahoo.co.jp/config/login");
    NavigableAuthenticationPage authPage = new NavigableAuthenticationPage(page, webClient);
//#username
    
    authPage.type(username, "#username");
    authPage.savePage("1-yahoo-auth", "yahoo", webpageService);
    authPage.click("#btnNext", webpageService);
    authPage.savePage("2-yahoo-auth", "yahoo", webpageService);
    authPage.typePassword(password, "#passwd"); //or #code
    authPage.savePage("3-yahoo-auth", "yahoo", webpageService);
    //authPage.typeCheckbox("off", "#persistent");
    //authPage.savePage("4-yahoo-auth", "yahoo", webpageService);
    authPage.click("#btnSubmit", webpageService);
    authPage.savePage("5-yahoo-auth", "yahoo", webpageService);
    //authPage.click("#skipButton", webpageService);
    //authPage.savePage("6-yahoo-auth", "yahoo", webpageService);

    //If we see the yahoo image, consider ourselves logged in
  //  HtmlElement yahooImg = authPage.getPage().querySelector("#masthead > h1:nth-child(1) > a:nth-child(1) > img:nth-child(1)");
    authPage.savePage("yahoo-authenticated", siteName, webpageService);
/*
    boolean loginSuccess;
    if(yahooImg != null) {
      System.out.println("Logged in successfully");
      loginSuccess = true;
    } else {
      System.out.println("Failed to login");
      loginSuccess = false;
    }
*/
//#SearchBoxHead
    //authPage.confirmLoginByElementExists("#masthead > h1:nth-child(1) > a:nth-child(1) > img:nth-child(1)");
    //authPage.confirmLoginByElementExists(".elLogo > a:nth-child(1) > img:nth-child(1)");
    //authPage.confirmLoginByElementExists("._3YIqBohnzWyU3NQ8zb-mQI > a:nth-child(1)");
    authPage.confirmLoginByMissingLoginText("#Login > div", "ログイン");

    return new YahooAuthenticationCrawlerResult(authPage.getLoginStatus(), null);
  }
}
