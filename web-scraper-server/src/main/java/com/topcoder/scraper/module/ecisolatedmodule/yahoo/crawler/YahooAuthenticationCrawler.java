package com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler;

import java.io.IOException;

import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.lib.navpage.NavigableAuthenticationPage;
import com.topcoder.scraper.service.WebpageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YahooAuthenticationCrawler {

  private Logger logger = LoggerFactory.getLogger(YahooAuthenticationCrawler.class.getName());
  
  private String siteName;
  
  private final WebpageService webpageService;
  
  public YahooAuthenticationCrawler(String siteName, WebpageService webpageService) {
    this.siteName = siteName;
    this.webpageService = webpageService;
  }



  public boolean authenticate(TrafficWebClient webClient, String username, String password) throws IOException {
    //NOTE: Make sure webClient.getOptions().setJavaScriptEnabled(true); in WebClientConfig.java!
    webClient.getWebClient().getCookieManager().clearCookies();
    
    NavigableAuthenticationPage authPage = new NavigableAuthenticationPage("https://login.yahoo.co.jp/config/login", webClient); //TODO: How to set this parameter?
    authPage.type(username, "#username");
    authPage.savePage("1-yahoo-auth", "yahoo", webpageService);
    authPage.click("#btnNext", webpageService);
    authPage.savePage("2-yahoo-auth", "yahoo", webpageService);
    authPage.typePassword(password, "#passwd"); //or #code
    authPage.savePage("3-yahoo-auth", "yahoo", webpageService);
    authPage.typeCheckbox("off", "#persistent");
    authPage.savePage("4-yahoo-auth", "yahoo", webpageService);
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
    authPage.ConfirmLoginByElementExists("#masthead > h1:nth-child(1) > a:nth-child(1) > img:nth-child(1)");
    return authPage.getLoginStatus();
  }
}
