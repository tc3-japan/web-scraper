package com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.CodeType;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.lib.navpage.NavigableAuthenticationPage;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractAuthenticationCrawler;
import com.topcoder.scraper.service.WebpageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Yahoo implementation of AuthenticationCrawler
 */
@Component
public class YahooAuthenticationCrawler extends AbstractAuthenticationCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooAuthenticationCrawler.class.getName());
  
  private String                      siteName;
  private NavigableAuthenticationPage authPage;

  private final WebpageService webpageService;

  public YahooAuthenticationCrawler(WebpageService webpageService) {
    this.siteName       = "yahoo";
    this.webpageService = webpageService;
  }

  // TODO : implement
  @Override
  public YahooAuthenticationCrawlerResult authenticate(TrafficWebClient webClient,
                                                       String username,
                                                       String password, String code, boolean init) throws IOException {
    return this.authenticate(webClient, username, password, code, false);
  }

  @Override
  public YahooAuthenticationCrawlerResult authenticate(TrafficWebClient webClient, String username, String password, String code) throws IOException {
    String path;

    // First Step and Second Step of Password Login
    if (StringUtils.isEmpty(code)) {
      // TODO: Make sure webClient.getOptions().setJavaScriptEnabled(true);

      webClient.getWebClient().getCookieManager().clearCookies();
      webClient.getWebClient().getOptions().setJavaScriptEnabled(true);

      HtmlPage page = webClient.getPage("https://login.yahoo.co.jp/config/login");
      authPage = new NavigableAuthenticationPage(page, webClient);
      authPage.type(username, "#username");
      authPage.savePage("yahoo-auth-1", "yahoo", webpageService);

      authPage.click("#btnNext", webpageService);
      path = authPage.savePage("yahoo-auth-2", "yahoo", webpageService);

      if (StringUtils.isNotEmpty(password)) {
        // password login
        authPage.typePassword(password, "#passwd");
      } else {
        // send verify code via SMS / EMail
        return new YahooAuthenticationCrawlerResult(false, CodeType.VerifyCodeLogin, path);
      }

    // Second Step of Verify Code Login
    } else if (StringUtils.isNotEmpty(code)) {
      // verify code login
      authPage.type(code, "#code");
    }
    authPage.savePage("yahoo-auth-3", "yahoo", webpageService);

    // Persistent Login: default value is on at current(2019-12) site , so we don't need this code.
    //authPage.typeCheckbox("on", "#persistent");
    //authPage.savePage("yahoo-auth-4", "yahoo", webpageService);

    authPage.click("#btnSubmit", webpageService);
    authPage.savePage("yahoo-auth-5", "yahoo", webpageService);

    path = authPage.savePage("yahoo-authenticated", siteName, webpageService);
    authPage.confirmLoginByMissingLoginText("#Login > div", "ログイン");

    return new YahooAuthenticationCrawlerResult(authPage.getLoginStatus(), path);
  }
}
