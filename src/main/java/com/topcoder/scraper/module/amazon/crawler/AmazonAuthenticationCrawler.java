package com.topcoder.scraper.module.amazon.crawler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlEmailInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.topcoder.scraper.config.AmazonProperty;
import com.topcoder.scraper.service.WebpageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Login amazon
 */
public class AmazonAuthenticationCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonAuthenticationCrawler.class);

  // Current authentication has a temporary solution for Amazon CAPTCHA.
  // When Amazon-site requests us to input CAPTCHA at login, we can put it into CAPTCHA_PATH file
  // and can pass the login with CAPTCHA.
  // We set CAPTCHA_TRIAL_COUNT_MAX that means trial count to login with CAPTCHA,
  // and set CAPTCHA_WAIT_COUNT_MAX that means wait count to check if we put the CAPTCHA code into the CAPTCHA_PATH,
  // and set CAPTCHA_WAIT_MILLI_SEC that means wait time[milli sec] to check above.
  // Those default values are decided somehow.
  private static final String CAPTCHA_PATH = "./captcha-input.txt";
  private static final int CAPTCHA_TRIAL_COUNT_MAX = 5;
  private static final int CAPTCHA_WAIT_COUNT_MAX = 20;
  private static final int CAPTCHA_WAIT_MILLI_SEC = 5000;

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

    // Save page
    webpageService.save("home", siteName, homePage.getWebResponse().getContentAsString());

    // Check CAPTCHA 1st
    HtmlInput capthcaInput1st = homePage.querySelector(property.getCrawling().getLoginPage().getCaptchaInput1st());
    if (capthcaInput1st != null) {
      LOGGER.info("Captcha 1st Input Found!!!");
      AmazonAuthenticationCrawlerResult result = handleCaptchaInput1st(homePage,1);
      if (result.isSuccess()) {
        homePage = webClient.getPage(property.getUrl());
      } else {
        return result;
      }
    }

    // click login button
    //HtmlPage loginPage = ((HtmlAnchor) homePage.getFirstByXPath("//*[@id=\"nav-link-accountList\"]")).click();
    HtmlPage loginPage = ((HtmlAnchor) homePage.querySelector(property.getCrawling().getLoginButton())).click();

    // Save page
    webpageService.save("login-email", siteName, loginPage.getWebResponse().getContentAsString());

    // Fill in email
    //HtmlEmailInput emailInput = loginPage.getFirstByXPath("//input[@id=\"ap_email\"]");
    HtmlEmailInput emailInput = loginPage.querySelector(property.getCrawling().getLoginPage().getEmailInput());
    emailInput.type(username);

    // Submit form
    //HtmlSubmitInput submitInput1 = loginPage.getFirstByXPath("//input[@id=\"continue\"]");
    HtmlSubmitInput submitInput1 = loginPage.querySelector(property.getCrawling().getLoginPage().getContinueInput());
    // continue button is optional, it shows sometimes
    if (submitInput1 != null) {
      loginPage = submitInput1.click();
    }

    // Save page
    webpageService.save("login-pass", siteName, loginPage.getWebResponse().getContentAsString());

    // Fill in password
    //HtmlPasswordInput passwordInput = passwordPage.getFirstByXPath("//input[@id=\"ap_password\"]");
    HtmlPasswordInput passwordInput = loginPage.querySelector(property.getCrawling().getLoginPage().getPasswordInput());
    passwordInput.type(password);

    // Fill in remember-me
    // *Experimental Code*
    HtmlCheckBoxInput rememberMeCheckInput = loginPage.querySelector("input[type='checkbox'][name='rememberMe']");
    rememberMeCheckInput.setChecked(true);
    LOGGER.info("Remember Me Check:" + rememberMeCheckInput.isChecked());

    // Submit form
    //HtmlSubmitInput submitInput2 = passwordPage.getFirstByXPath("//input[@id=\"signInSubmit\"]");
    HtmlSubmitInput submitInput2 = loginPage.querySelector(property.getCrawling().getLoginPage().getSubmitButton());

    HtmlPage finalPage = submitInput2.click();

    // Save page
    String path = webpageService.save("login", siteName, finalPage.getWebResponse().getContentAsString());

    // Check Login Successfully
    HtmlEmailInput emailInputCheck = finalPage.querySelector(property.getCrawling().getLoginPage().getEmailInput());
    if (emailInputCheck != null) {
      // still in login page

      HtmlTextInput captchaInput = finalPage.querySelector(property.getCrawling().getLoginPage().getCaptchaInput2nd());
      if (captchaInput != null) {
        LOGGER.info("Captcha 2nd Input Found!!!");
        return handleCaptchaInput2nd(finalPage, password,1);
      }

      return new AmazonAuthenticationCrawlerResult(false, path);
    }

    return new AmazonAuthenticationCrawlerResult(true, path);
  }

  private AmazonAuthenticationCrawlerResult handleCaptchaInput1st(HtmlPage page, int trialCount) throws IOException {
    LOGGER.info("Handle Captcha Input 1st. Trial Count: " + trialCount);

    // Save CAPTCHA Image
    // see: https://stackoverflow.com/questions/3068543/how-to-get-base64-encoded-contents-for-an-imagereader
    HtmlImage htmlImg  = (HtmlImage) page.querySelector(property.getCrawling().getLoginPage().getCaptchaImage1st());
    webpageService.saveImage("captcha-1st", "png", siteName, htmlImg);

    // Fill in captcha
    String captchaStr = readCaptchaFromFile();
    HtmlTextInput captchaInput = page.querySelector(property.getCrawling().getLoginPage().getCaptchaInput1st());
    captchaInput.type(captchaStr);

    // Submit form
    HtmlButton submit = page.querySelector(property.getCrawling().getLoginPage().getCaptchaSubmit1st());
    HtmlPage page2 = submit.click();

    // Save page
    String path = webpageService.save("login-captcha1st", siteName, page2.getWebResponse().getContentAsString());

    // captcha input check
    HtmlTextInput captchaInputCheck = page2.querySelector(property.getCrawling().getLoginPage().getCaptchaInput1st());
    if (captchaInputCheck == null) {
      // success case
      return new AmazonAuthenticationCrawlerResult(true, path);
    } else {
      // failure case
      if (trialCount >= CAPTCHA_TRIAL_COUNT_MAX) {
        // retry out
        return new AmazonAuthenticationCrawlerResult(false, path);
      }
      // retry
      return handleCaptchaInput1st(page2, trialCount+1);
    }
  }

  private AmazonAuthenticationCrawlerResult handleCaptchaInput2nd(HtmlPage page, String password, int trialCount) throws IOException {
    LOGGER.info("Handle Captcha Input 2nd. Trial Count: " + trialCount);

    // Save CAPTCHA Image
    // see: https://stackoverflow.com/questions/3068543/how-to-get-base64-encoded-contents-for-an-imagereader
    HtmlImage htmlImg  = (HtmlImage) page.querySelector(property.getCrawling().getLoginPage().getCaptchaImage2nd());
    webpageService.saveImage("captcha-2nd", "png", siteName, htmlImg);

    // Fill in password
    HtmlPasswordInput passwordInput = page.querySelector(property.getCrawling().getLoginPage().getPasswordInput());
    passwordInput.type(password);

    // Fill in captcha
    String captchaStr = readCaptchaFromFile();
    HtmlTextInput captchaInput = page.querySelector(property.getCrawling().getLoginPage().getCaptchaInput2nd());
    captchaInput.type(captchaStr);

    // Submit form
    HtmlSubmitInput submitInput = page.querySelector(property.getCrawling().getLoginPage().getSubmitButton());
    HtmlPage page2 = submitInput.click();

    // Save page
    String path = webpageService.save("login-captcha2nd", siteName, page2.getWebResponse().getContentAsString());

    // captcha input check
    HtmlTextInput captchaInputCheck = page2.querySelector(property.getCrawling().getLoginPage().getCaptchaInput2nd());
    if (captchaInputCheck == null) {
      // success case
      return new AmazonAuthenticationCrawlerResult(true, path);
    } else {
      // failure case
      if (trialCount >= CAPTCHA_TRIAL_COUNT_MAX) {
        // retry out
        return new AmazonAuthenticationCrawlerResult(false, path);
      }
      // retry
      return handleCaptchaInput2nd(page2, password, trialCount+1);
    }
  }

  private String readCaptchaFromFile() throws IOException {
    File captchaInputFile = new File(CAPTCHA_PATH);
    int waitCount = CAPTCHA_WAIT_COUNT_MAX;

    while (!captchaInputFile.exists() && waitCount > 0) {
      LOGGER.info("Please write capture characters in " + CAPTCHA_PATH + ". sleep " + CAPTCHA_WAIT_MILLI_SEC + " milliseconds, count:" + waitCount);
      // Open CAPTCHA file and see CAPTCHA characters.
      // $ echo '<captcha_characters>' > ./captcha-input.txt
      try {
        Thread.sleep(CAPTCHA_WAIT_MILLI_SEC);
      } catch(InterruptedException e){
        LOGGER.info("Error occurs while sleeping.");
        LOGGER.info(e.getClass() + ":" + e.getMessage());
        //e.printStackTrace();
      }
      captchaInputFile = new File(CAPTCHA_PATH);
      waitCount -= 1;
    }
    if (captchaInputFile.exists()) {
      String line  = new BufferedReader(new FileReader(captchaInputFile)).readLine();
      captchaInputFile.delete();
      LOGGER.info("Got the captcha input: " + line);
      return line;
    } else {
      LOGGER.info("There is no captcha file.");
      return null;
    }
  }

}
