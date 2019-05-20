package com.topcoder.scraper.module.kojima.crawler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.service.WebpageService;

public class KojimaAuthenticationCrawler {

  private Logger logger = LoggerFactory.getLogger(KojimaAuthenticationCrawler.class.getName());
  
  private String siteName;
  
  private final WebpageService webpageService;
  
  public KojimaAuthenticationCrawler(String siteName, WebpageService webpageService) {
    this.siteName = siteName;
    this.webpageService = webpageService;
  }



  public boolean authenticate(TrafficWebClient webClient, String username, String password) throws IOException {
    
    webClient.getWebClient().getCookieManager().clearCookies();
    
    HtmlPage loginPage = webClient.getPage("https://www.kojima.net/ec/member/CSfLogin.jsp");

    webpageService.save("kojima-login", siteName, loginPage.getWebResponse().getContentAsString());

    HtmlForm loginForm = loginPage.getFormByName("MemberForm");
    
    if (loginForm == null) {
      throw new RuntimeException("The form is not found in the login page. The site might be changed.");
    }
    
    HtmlTextInput memberIdInput = loginForm.getInputByName("MEM_ID");
    HtmlPasswordInput passwordInput = loginForm.getInputByName("PWD");
    HtmlCheckBoxInput rememberInput = loginForm.getInputByName("REM");
    HtmlImageInput loginButtonInput = loginPage.querySelector("div.member-login>div.member-login-inner>input.imgover");
    
    
    memberIdInput.type(username);
    passwordInput.type(password);
    rememberInput.type("on");
    HtmlPage afterLoginPage = webClient.click(loginButtonInput);

    webpageService.save("kojima-after-login", siteName, afterLoginPage.getWebResponse().getContentAsString());
    
    try {
      loginForm = afterLoginPage.getFormByName("MemberForm");
      return loginForm == null;
    } catch (ElementNotFoundException e) {
      return true;
    }
  }
}
