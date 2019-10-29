package com.topcoder.scraper.module.ecisolatedmodule.kojima.crawler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.lib.navpage.NavigableAuthenticationPage;
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
    
    //HtmlPage loginPage = webClient.getPage("https://www.kojima.net/ec/member/CSfLogin.jsp");
    webClient.getWebClient().getOptions().setJavaScriptEnabled(true);
    NavigableAuthenticationPage authPage = new NavigableAuthenticationPage("https://www.kojima.net/ec/member/CSfLogin.jsp", webClient);
    webpageService.save("kojima-login", siteName, authPage.getPage().getWebResponse().getContentAsString());

    //HtmlForm loginForm = loginPage.getFormByName("MemberForm");
    
    //if (loginForm == null) {
    //  throw new RuntimeException("The form is not found in the login page. The site might be changed.");
    //}
    
    //HtmlTextInput memberIdInput = loginForm.getInputByName("MEM_ID");
    authPage.type(username, "MEM_ID");
    //HtmlPasswordInput passwordInput = loginForm.getInputByName("PWD");
    authPage.typePassword(password, "#PWD");

    //HtmlElement rememberInput = loginForm.getInputByName("REM");
    authPage.type("on", "REM");
    //HtmlElement loginButtonInput = loginPage.querySelector("div.member-login>div.member-login-inner>input.imgover");
    //if (loginButtonInput == null) loginButtonInput = loginPage.querySelector(".login_box01 > p:nth-child(4) > button:nth-child(1)");
    //System.out.println();
    //System.out.println("memberIdInput: " + memberIdInput);
    //System.out.println("passwordInput: " + passwordInput);
    //System.out.println("loginButtonInput: " + loginButtonInput);

    //memberIdInput.type(username);
    //passwordInput.type(password); 
    //rememberInput.type("on");
    
    //HtmlPage afterLoginPage = webClient.click(loginButtonInput);
    //.login_box01 > p:nth-child(4) > button:nth-child(1)
    //.login_box01 > p:nth-child(4)
    authPage.click("div.member-login>div.member-login-inner>input.imgover");
    //authPage.click(".login_box01 > p:nth-child(4) > button:nth-child(1)");
    //authPage.click(".login_box01 > p:nth-child(4)");
    HtmlPage afterLoginPage = authPage.getPage();
    webpageService.save("kojima-after-login", siteName, afterLoginPage.getWebResponse().getContentAsString());
    
    try {
      HtmlForm memberForm = afterLoginPage.getFormByName("MemberForm");
      return memberForm == null;
    } catch (ElementNotFoundException e) {
      return true;
    }
  }
}
