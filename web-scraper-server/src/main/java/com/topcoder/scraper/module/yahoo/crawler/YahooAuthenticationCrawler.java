package com.topcoder.scraper.module.yahoo.crawler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.service.WebpageService;

public class YahooAuthenticationCrawler {

  private Logger logger = LoggerFactory.getLogger(YahooAuthenticationCrawler.class.getName());
  
  private String siteName;
  
  private final WebpageService webpageService;
  
  public YahooAuthenticationCrawler(String siteName, WebpageService webpageService) {
    this.siteName = siteName;
    this.webpageService = webpageService;
  }



  public boolean authenticate(TrafficWebClient webClient, String username, String password) throws IOException {
    
    webClient.getWebClient().getCookieManager().clearCookies();
    
    HtmlPage loginPage = webClient.getPage("https://login.yahoo.co.jp/config/login");

    //Warning: Username comes on first page unless autologin / remember me
    HtmlTextInput memberIdInput = loginPage.querySelector("#username");//loginForm.getInputByName("MEM_ID");
    memberIdInput.type(username);
    HtmlButton nextButton = loginPage.querySelector("#btnNext");//#btnNext
    //System.out.println(">>>>> INPUTTED: " + memberIdInput.getText());
    webpageService.save("yahoo-login-initial", siteName, loginPage.getWebResponse().getContentAsString()); //TODO
    //System.out.println(">>>>> BUTTON: " + nextButton.asText());
    HtmlPage passwordPage = webClient.click(nextButton);

    //System.out.println(">>>> PW PAGE: " + passwordPage.getWebResponse().getWebRequest().getUrl());

    webpageService.save("yahoo-password-page-login", siteName, passwordPage.getWebResponse().getContentAsString());

    if(passwordPage!=null) {} else {} //else use login page because username was already remembered and you were at password login page not user email login page}
    HtmlPasswordInput passwordInput = passwordPage.querySelector("#passwd");//loginForm.getInputByName("PWD");
    HtmlCheckBoxInput rememberInput = passwordPage.querySelector("#persistent");//loginForm.getInputByName("REM");
    HtmlButton loginButtonInput = passwordPage.querySelector("#btnSubmit");//loginPage.querySelector("div.member-login>div.member-login-inner>input.imgover");
    //System.out.println("\n\n\n>>>> loginBtn: " + loginButtonInput.asText());
    
    //memberIdInput.type(username);
    passwordInput.type(password);
    rememberInput.type("off");
    HtmlPage afterLoginPage = webClient.click(loginButtonInput);

    HtmlButton skipThisPageButton = afterLoginPage.querySelector("#skipButton");
    HtmlPage finalPage;
    if (skipThisPageButton != null) { //TODO: null is NOT the right check here?
      finalPage = webClient.click(skipThisPageButton); 
    } else {
      finalPage = afterLoginPage;
    }
    //webpageService.save("yahoo-after-login", siteName, afterLoginPage.getWebResponse().getContentAsString()); //TODO
    webpageService.save("yahoo-final-page", siteName, finalPage.getWebResponse().getContentAsString()); //TODO


    //If we see the yahoo image, consider ourselves logged in
    HtmlElement yahooImg = finalPage.querySelector("#masthead > h1:nth-child(1) > a:nth-child(1) > img:nth-child(1)");
    //System.out.println(">>>>>> yahooImg " + yahooImg);
    
    boolean loginSuccess;
    if(yahooImg != null) {
      System.out.println("Logged in successfully");
      loginSuccess = true;
    } else {
      System.out.println("Failed to login");
      loginSuccess = false;
    }

    return loginSuccess; //TODO: Detect if login failed
  }
}
