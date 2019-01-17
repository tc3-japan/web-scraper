package com.topcoder.scraper.module.amazon.crawler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlEmailInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.topcoder.scraper.config.AmazonProperty;
import com.topcoder.scraper.service.WebpageService;
import java.io.IOException;

/**
 * Login amazon
 */
public class AmazonAuthenticationCrawler {

  private String siteName;
  private AmazonProperty property;
  private final WebpageService webpageService;

  public AmazonAuthenticationCrawler(String siteName, AmazonProperty property, WebpageService webpageService) {
    this.siteName = siteName;
    this.property = property;
    this.webpageService = webpageService;
  }

  /**
   * Login amazon
   * @param webClient the web client
   * @param username the username
   * @param password the password
   * @return AmazonAuthenticationCrawlerResult
   * @throws IOException
   */
  public AmazonAuthenticationCrawlerResult authenticate(WebClient webClient, String username, String password) throws IOException {

    webClient.getCookieManager().clearCookies();

    // Fetch homepage
    HtmlPage homePage = webClient.getPage(property.getUrl());

    // click login button
    //HtmlPage loginPage = ((HtmlAnchor) homePage.getFirstByXPath("//*[@id=\"nav-link-accountList\"]")).click();
    HtmlPage loginPage = ((HtmlAnchor) homePage.querySelector(property.getCrawling().getLoginButton())).click();

    // Fill in email
    //HtmlEmailInput input = loginPage.getFirstByXPath("//input[@id=\"ap_email\"]");
    HtmlEmailInput input = loginPage.querySelector(property.getCrawling().getLoginPage().getEmailInput());
    input.type(username);

    // Submit form
    //HtmlSubmitInput submitInput1 = loginPage.getFirstByXPath("//input[@id=\"continue\"]");
    HtmlSubmitInput submitInput1 = loginPage.querySelector(property.getCrawling().getLoginPage().getContinueInput());
    // continue button is optional, it shows sometimes
    if (submitInput1 != null) {
      loginPage = submitInput1.click();
    }

    // Fill in password
    //HtmlPasswordInput passwordInput = passwordPage.getFirstByXPath("//input[@id=\"ap_password\"]");
    HtmlPasswordInput passwordInput = loginPage.querySelector(property.getCrawling().getLoginPage().getPasswordInput());
    passwordInput.type(password);

    // Submit form
    //HtmlSubmitInput submitInput2 = passwordPage.getFirstByXPath("//input[@id=\"signInSubmit\"]");
    HtmlSubmitInput submitInput2 = loginPage.querySelector(property.getCrawling().getLoginPage().getSubmitButton());

    HtmlPage finalPage = submitInput2.click();

    // Save page
    String path = webpageService.save("login", siteName, finalPage.getWebResponse().getContentAsString());

    HtmlEmailInput emailInputCheck = finalPage.querySelector(property.getCrawling().getLoginPage().getEmailInput());
    if (emailInputCheck != null) {
      // still in login page
      return new AmazonAuthenticationCrawlerResult(false, path);
    }

    return new AmazonAuthenticationCrawlerResult(true, path);
  }

}
