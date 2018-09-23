package com.topcoder.scraper.module.amazon;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlEmailInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.topcoder.scraper.config.AmazonProperty;
import com.topcoder.scraper.module.AuthenticationModule;
import com.topcoder.scraper.service.WebpageService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Amazon implementation for AuthenticationModule
 */
@Component
public class AmazonAuthenticationModule extends AuthenticationModule {

  private final AmazonProperty property;
  private final WebClient webClient;
  private final WebpageService webpageService;

  @Autowired
  public AmazonAuthenticationModule(
    AmazonProperty property,
    WebClient webClient,
    WebpageService webpageService) {
    this.property = property;
    this.webClient = webClient;
    this.webpageService = webpageService;
  }

  @Override
  public String getECName() {
    return "amazon";
  }

  /**
   * Implementation of authenticate method
   */
  @Override
  public void authenticate() throws IOException {
    // Fetch homepage
    HtmlPage homePage = webClient.getPage(property.getUrl());

    // click login button
    HtmlPage loginPage = ((HtmlAnchor) homePage.getFirstByXPath("//*[@id=\"nav-link-accountList\"]")).click();

    // Fill in email
    HtmlEmailInput input = loginPage.getFirstByXPath("//input[@id=\"ap_email\"]");
    input.type(property.getUsername());

    // Submit form
    HtmlSubmitInput submitInput1 = loginPage.getFirstByXPath("//input[@id=\"continue\"]");
    HtmlPage passwordPage = submitInput1.click();

    // Fill in password
    HtmlPasswordInput passwordInput = passwordPage.getFirstByXPath("//input[@id=\"ap_password\"]");
    passwordInput.type(property.getPassword());

    // Submit form
    HtmlSubmitInput submitInput2 = passwordPage.getFirstByXPath("//input[@id=\"signInSubmit\"]");

    HtmlPage finalPage = submitInput2.click();

    // Save page
    webpageService.save(getECName(), finalPage.getWebResponse().getContentAsString());
  }
}
