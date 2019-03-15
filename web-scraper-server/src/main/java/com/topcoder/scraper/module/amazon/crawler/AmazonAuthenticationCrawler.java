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
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.topcoder.api.entity.CodeType;
import com.topcoder.scraper.config.AmazonProperty;
import com.topcoder.scraper.module.AuthStep;
import com.topcoder.scraper.service.WebpageService;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Login amazon
 */
public class AmazonAuthenticationCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonAuthenticationCrawler.class);

  class SubmitResult {

    private boolean success;
    private HtmlPage htmlPage;
    private String htmlPath;

    public SubmitResult(boolean success, HtmlPage htmlPage, String htmlPath) {
      this.success = success;
      this.htmlPage = htmlPage;
      this.htmlPath = htmlPath;
    }

    public boolean isSuccess() {
      return success;
    }

    public HtmlPage getHtmlPage() {
      return htmlPage;
    }

    public String getHtmlPath() {
      return htmlPath;
    }
  }

  // Current authentication has a temporary solution for login with CAPTCHA / Verification Code Needed.
  //   When Amazon-site requests us to input CAPTCHA / Verification Code at login, we can put it into CODE_PATH file
  //   and we can pass the login with CAPTCHA / Verification Code Needed.
  // We set CAPTCHA_TRIAL_COUNT_MAX that means trial count to login with CAPTCHA,
  //   and set VERIFY_TRIAL_COUNT_MAX that means trial count to login with Verification Code Needed,
  //   and set CODE_WAIT_COUNT_MAX that means wait count to check if we put the CAPTCHA code into the CODE_PATH,
  //   and set CODE_WAIT_MILLI_SEC that means wait time[milli sec] to check above.
  // Those default values are decided somehow.
  private static final String CODE_PATH = "./code-input.txt";
  private static final int CAPTCHA_TRIAL_COUNT_MAX = 5;
  private static final int VERIFY_TRIAL_COUNT_MAX = 5;
  private static final int CODE_WAIT_COUNT_MAX = 20;
  private static final int CODE_WAIT_MILLI_SEC = 5000;

  private String siteName;
  private AmazonProperty property;
  private final WebpageService webpageService;
  private HtmlPage finalPage;
  private HtmlPage homePage;
  private String path;

  private Logger logger = LoggerFactory.getLogger(AmazonAuthenticationCrawler.class.getName());

  @Getter
  @Setter
  private AuthStep authStep = AuthStep.FIRST;

  public AmazonAuthenticationCrawler(String siteName, AmazonProperty property, WebpageService webpageService) {
    this.siteName = siteName;
    this.property = property;
    this.webpageService = webpageService;
  }


  /**
   * authentication used by rest login/login_init endpoint
   *
   * @param webClient the web client from rest context
   * @param username  the user name
   * @param password  the password
   * @param code      the code
   * @param init      is only used for login_init ?
   * @throws Exception if any error happened
   */
  public AmazonAuthenticationCrawlerResult authenticate(WebClient webClient,
                                                        String username,
                                                        String password, String code, boolean init) throws Exception {
    if (homePage == null) {
      webClient.getCookieManager().clearCookies();
      // Fetch homepage
      homePage = webClient.getPage(property.getUrl());
      // Save page
      webpageService.save("home", siteName, homePage.getWebResponse().getContentAsString());
    }

    if (authStep == AuthStep.FIRST) {
      logger.info("start auth step 1");
      // Check CAPTCHA 1st
      HtmlInput captchaInput1st = homePage.querySelector(property.getCrawling().getLoginPage().getCaptchaInput1st());
      if (captchaInput1st != null) {
        if (code == null) { // return image to client
          LOGGER.info("Captcha 1st Input Found, at step = " + authStep);
          HtmlImage image = homePage.querySelector(property.getCrawling().getLoginPage().getCaptchaImage1st());
          return new AmazonAuthenticationCrawlerResult(false,
            "CAPTCHA code needed on home page", CodeType.CAPTCHA, webpageService.toBase64Image(image), true);
        } else { // fill code
          SubmitResult result = handleCaptchaInput1st(homePage, code);
          homePage = webClient.getPage(property.getUrl());
          if (result.isSuccess()) {     // succeed
            authStep = AuthStep.SECOND; // goto step2
          } else { // failed, return code again
            HtmlImage image = homePage.querySelector(property.getCrawling().getLoginPage().getCaptchaImage1st());
            return new AmazonAuthenticationCrawlerResult(false,
              "CAPTCHA code needed on home page", CodeType.CAPTCHA, webpageService.toBase64Image(image), true);
          }
        }
      } else {
        authStep = AuthStep.SECOND; // goto step 2
        if (init) { // for login init
          return new AmazonAuthenticationCrawlerResult(true, null);
        }
      }
    }


    if (authStep == AuthStep.SECOND) {
      logger.info("start auth step 2");
      if (code == null) { // fill login
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

        finalPage = submitInput2.click();

        // Save page
        webpageService.save("login-click", siteName, finalPage.getWebResponse().getContentAsString());

        // Check Login Successfully > CAPTCHA
        HtmlEmailInput emailInputCheck = finalPage.querySelector(property.getCrawling().getLoginPage().getEmailInput());
        if (emailInputCheck != null) {
          // still in login page
          HtmlTextInput captchaInput = finalPage.querySelector(property.getCrawling().getLoginPage().getCaptchaInput2nd());
          if (captchaInput != null) {
            LOGGER.info("Captcha 2nd Input Found, at step " + authStep);
            HtmlImage htmlImg = finalPage.querySelector(property.getCrawling().getLoginPage().getCaptchaImage2nd());
            return new AmazonAuthenticationCrawlerResult(false,
              "CAPTCHA code needed on login page", CodeType.CAPTCHA, webpageService.toBase64Image(htmlImg), true);
          } else {
            authStep = AuthStep.LAST; // goto last step
          }
        } else {
          authStep = AuthStep.LAST; // goto last step
        }
      } else { // fill code
        logger.info("start check Captcha in step 2 with code = " + code);
        SubmitResult result = handleCaptchaInput2st(finalPage, password, code);
        finalPage = result.getHtmlPage();

        if (result.isSuccess()) {
          path = result.getHtmlPath();
          authStep = AuthStep.LAST; // goto last step
          code = null; // and clear code
        } else { // failed
          logger.info("check Captcha in step2 failed, now return error to frontend again");
          HtmlImage htmlImg = finalPage.querySelector(property.getCrawling().getLoginPage().getCaptchaImage2nd());
          return new AmazonAuthenticationCrawlerResult(false,
            "CAPTCHA code needed on Login page", CodeType.CAPTCHA, webpageService.toBase64Image(htmlImg), true);
        }
      }
    }

    if (authStep == AuthStep.LAST) {
      if (code == null) {
        // Check Login Successfully > Verification Code Needed
        HtmlRadioButtonInput smsInputCheck = finalPage.querySelector("input[type='radio'][name='option']");
        if (smsInputCheck != null) {
          LOGGER.info("Verification Code Needed, at step = " + authStep);
          return new AmazonAuthenticationCrawlerResult(false, "Verification Code Needed",
            CodeType.Verification, null, true);
        } else {
          authStep = AuthStep.DONE;
        }
      } else { // fill code
        SubmitResult result = this.fillVerificationCodeNeeded(finalPage, code);
        if (result != null && result.isSuccess()) {
          authStep = AuthStep.DONE;
        } else {
          LOGGER.info("Verification Code check failed, at step = " + authStep);
          // here Verification code check failed, we cannot continue
          return new AmazonAuthenticationCrawlerResult(false, "Verification Code Needed",
            CodeType.Verification, null, true);
        }
      }
    }

    if (authStep == AuthStep.DONE) {
      // Save page
      webpageService.save("login-done", siteName, finalPage.getWebResponse().getContentAsString());
      return new AmazonAuthenticationCrawlerResult(true, path);
    }

    return new AmazonAuthenticationCrawlerResult(false, "UNKNOWN ERROR HAPPENED");
  }

  /**
   * Login amazon
   *
   * @param webClient the web client
   * @param username  the username
   * @param password  the password
   * @return AmazonAuthenticationCrawlerResult
   * @throws IOException
   */
  public AmazonAuthenticationCrawlerResult authenticate(WebClient webClient,
                                                        String username,
                                                        String password) throws IOException {

    webClient.getCookieManager().clearCookies();


    // Fetch homepage
    HtmlPage homePage = webClient.getPage(property.getUrl());
    System.out.println(property.getUrl());

    // Save page
    webpageService.save("home", siteName, homePage.getWebResponse().getContentAsString());

    // Check CAPTCHA 1st
    HtmlInput capthcaInput1st = homePage.querySelector(property.getCrawling().getLoginPage().getCaptchaInput1st());
    if (capthcaInput1st != null) {
      LOGGER.info("Captcha 1st Input Found!!!");
      SubmitResult result = handleCaptchaInput1st(homePage, 1);
      if (result.isSuccess()) {
        homePage = webClient.getPage(property.getUrl());
      } else {
        return new AmazonAuthenticationCrawlerResult(result.isSuccess(), result.htmlPath);
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

    finalPage = submitInput2.click();

    // Save page
    String path = webpageService.save("login", siteName, finalPage.getWebResponse().getContentAsString());

    // Check Login Successfully > CAPTCHA
    SubmitResult result;
    HtmlEmailInput emailInputCheck = finalPage.querySelector(property.getCrawling().getLoginPage().getEmailInput());
    if (emailInputCheck != null) {
      // still in login page

      HtmlTextInput captchaInput = finalPage.querySelector(property.getCrawling().getLoginPage().getCaptchaInput2nd());
      if (captchaInput != null) {
        LOGGER.info("Captcha 2nd Input Found!!!");
        result = handleCaptchaInput2nd(finalPage, password, 1);
        if (result.isSuccess()) {
          finalPage = result.getHtmlPage();
        } else {
          return new AmazonAuthenticationCrawlerResult(false, result.getHtmlPath());
        }
      }
    }
    // Check Login Successfully > Verification Code Needed
    HtmlRadioButtonInput smsInputCheck = finalPage.querySelector("input[type='radio'][name='option']");
    if (smsInputCheck != null) {
      LOGGER.info("Verification Code Needed!!!");
      result = handleVerificationCodeNeeded(finalPage, 1);
      if (result.isSuccess()) {
        finalPage = result.getHtmlPage();
        path = result.getHtmlPath();
      } else {
        return new AmazonAuthenticationCrawlerResult(false, result.getHtmlPath());
      }
    }
    return new AmazonAuthenticationCrawlerResult(true, path);
  }


  private SubmitResult handleCaptchaInput1st(HtmlPage page, String code) throws IOException {
    // Fill in captcha
    HtmlTextInput captchaInput = page.querySelector(property.getCrawling().getLoginPage().getCaptchaInput1st());
    captchaInput.type(code);

    // Submit form
    HtmlButton submit = page.querySelector(property.getCrawling().getLoginPage().getCaptchaSubmit1st());
    HtmlPage page2 = submit.click();

    // Save page
    String path = webpageService.save("login-captcha1st", siteName, page2.getWebResponse().getContentAsString());

    // captcha input check
    HtmlTextInput captchaInputCheck = page2.querySelector(property.getCrawling().getLoginPage().getCaptchaInput1st());
    if (captchaInputCheck == null) {
      // success case
      return new SubmitResult(true, page2, path);
    } else {
      return new SubmitResult(false, page2, path);
    }
  }

  private SubmitResult handleCaptchaInput1st(HtmlPage page, int trialCount) throws IOException {
    LOGGER.info("Handle Captcha Input 1st. Trial Count: " + trialCount);

    // Save CAPTCHA Image
    // see: https://stackoverflow.com/questions/3068543/how-to-get-base64-encoded-contents-for-an-imagereader
    HtmlImage htmlImg = (HtmlImage) page.querySelector(property.getCrawling().getLoginPage().getCaptchaImage1st());
    webpageService.saveImage("captcha-1st", "png", siteName, htmlImg);

    // Fill in captcha
    String captchaStr = readCodeFromFile();
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
      return new SubmitResult(true, page2, path);
    } else {
      // failure case
      if (trialCount >= CAPTCHA_TRIAL_COUNT_MAX) {
        // retry out
        return new SubmitResult(false, page2, path);
      }
      // retry
      return handleCaptchaInput1st(page2, trialCount + 1);
    }
  }

  private SubmitResult handleCaptchaInput2st(HtmlPage page, String password, String code) throws IOException {

    // Fill in password
    HtmlPasswordInput passwordInput = page.querySelector(property.getCrawling().getLoginPage().getPasswordInput());
    passwordInput.type(password);

    // Fill in captcha
    HtmlTextInput captchaInput = page.querySelector(property.getCrawling().getLoginPage().getCaptchaInput2nd());
    captchaInput.type(code);

    // Submit form
    HtmlSubmitInput submitInput = page.querySelector(property.getCrawling().getLoginPage().getSubmitButton());
    HtmlPage page2 = submitInput.click();

    // Save page
    String path = webpageService.save("login-captcha2nd", siteName, page2.getWebResponse().getContentAsString());

    // captcha input check
    HtmlTextInput captchaInputCheck = page2.querySelector(property.getCrawling().getLoginPage().getCaptchaInput2nd());
    if (captchaInputCheck == null) {
      // success case
      return new SubmitResult(true, page2, path);
    } else {
      return new SubmitResult(false, page2, path);
    }
  }

  private SubmitResult handleCaptchaInput2nd(HtmlPage page, String password, int trialCount) throws IOException {
    LOGGER.info("Handle Captcha Input 2nd. Trial Count: " + trialCount);

    // Save CAPTCHA Image
    // see: https://stackoverflow.com/questions/3068543/how-to-get-base64-encoded-contents-for-an-imagereader
    HtmlImage htmlImg = (HtmlImage) page.querySelector(property.getCrawling().getLoginPage().getCaptchaImage2nd());
    webpageService.saveImage("captcha-2nd", "png", siteName, htmlImg);

    // Fill in password
    HtmlPasswordInput passwordInput = page.querySelector(property.getCrawling().getLoginPage().getPasswordInput());
    passwordInput.type(password);

    // Fill in captcha
    String captchaStr = readCodeFromFile();
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
      return new SubmitResult(true, page2, path);
    } else {
      // failure case
      if (trialCount >= CAPTCHA_TRIAL_COUNT_MAX) {
        // retry out
        return new SubmitResult(false, page2, path);
      }
      // retry
      return handleCaptchaInput2nd(page2, password, trialCount + 1);
    }
  }


  private SubmitResult fillVerificationCodeNeeded(HtmlPage page, String code) throws IOException {

    // Continue Submit form
    HtmlSubmitInput continueSubmitInput1 = page.querySelector("input#continue");
    HtmlPage page1 = continueSubmitInput1.click();

    // Fill in Verification Code
    HtmlTextInput codeInput = page1.querySelector("input[type='text'][name='code']");
    codeInput.type(code);

    // Submit form
    // TODO: select correct selector and delete others
    HtmlSubmitInput continueSubmitInput2 = page1.querySelector("#a-autoid-0 > span > input");
    if (continueSubmitInput2 == null) continueSubmitInput2 = page1.querySelector("span#a-autoid-0 > span > input");
    if (continueSubmitInput2 == null) continueSubmitInput2 = page1.querySelector("input[type='submit']");
    HtmlPage page2 = continueSubmitInput2.click();

    // Save page
    String path2 = webpageService.save("login-verify-submit", siteName, page2.getWebResponse().getContentAsString());

    // code input check
    HtmlTextInput codeInputCheck = page2.querySelector("input[type='text'][name='code']");
    if (codeInputCheck == null) {
      // success case
      return new SubmitResult(true, page2, path2);
    } else {
      return null;
    }
  }

  private SubmitResult handleVerificationCodeNeeded(HtmlPage page, int trialCount) throws IOException {
    LOGGER.info("Handle Verification Code Needed. Trial Count: " + trialCount);

    // Continue Submit form
    HtmlSubmitInput continueSubmitInput1 = page.querySelector("input#continue");
    HtmlPage page1 = continueSubmitInput1.click();

    // Save page
    String path1 = webpageService.save("login-verify-sms", siteName, page1.getWebResponse().getContentAsString());

    // Fill in Verification Code
    String codeStr = readCodeFromFile();
    HtmlTextInput codeInput = page1.querySelector("input[type='text'][name='code']");
    codeInput.type(codeStr);

    // Submit form
    // TODO: select correct selector and delete others
    HtmlSubmitInput continueSubmitInput2 = page1.querySelector("#a-autoid-0 > span > input");
    if (continueSubmitInput2 == null) continueSubmitInput2 = page1.querySelector("span#a-autoid-0 > span > input");
    if (continueSubmitInput2 == null) continueSubmitInput2 = page1.querySelector("input[type='submit']");
    HtmlPage page2 = continueSubmitInput2.click();

    // Save page
    String path2 = webpageService.save("login-verify-submit", siteName, page2.getWebResponse().getContentAsString());

    // code input check
    HtmlTextInput codeInputCheck = page2.querySelector("input[type='text'][name='code']");
    if (codeInputCheck == null) {
      // success case
      return new SubmitResult(true, page2, path2);
    } else {
      // failure case
      if (trialCount >= VERIFY_TRIAL_COUNT_MAX) {
        // retry out
        return new SubmitResult(false, page2, path2);
      }
      // retry
      return handleVerificationCodeNeeded(page2, trialCount + 1);
    }
  }

  private String readCodeFromFile() throws IOException {
    File codeInputFile = new File(CODE_PATH);
    int waitCount = CODE_WAIT_COUNT_MAX;

    while (!codeInputFile.exists() && waitCount > 0) {
      LOGGER.info("Please write code in " + CODE_PATH + ". sleep " + CODE_WAIT_MILLI_SEC + " milliseconds, count:" + waitCount);
      // Open CODE file and see CODE characters.
      // $ echo '<code_characters>' > ./code-input.txt
      try {
        Thread.sleep(CODE_WAIT_MILLI_SEC);
      } catch (InterruptedException e) {
        LOGGER.info("Error occurs while sleeping.");
        LOGGER.info(e.getClass() + ":" + e.getMessage());
        //e.printStackTrace();
      }
      codeInputFile = new File(CODE_PATH);
      waitCount -= 1;
    }
    if (codeInputFile.exists()) {
      String line = new BufferedReader(new FileReader(codeInputFile)).readLine();
      codeInputFile.delete();
      LOGGER.info("Got the input code: " + line);
      return line;
    } else {
      LOGGER.info("There is no code file: " + CODE_PATH);
      return null;
    }
  }

}
