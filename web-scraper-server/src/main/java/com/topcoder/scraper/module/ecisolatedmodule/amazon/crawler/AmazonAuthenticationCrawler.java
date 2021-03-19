package com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.*;
import com.topcoder.scraper.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.model.CodeType;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractAuthenticationCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.AuthStep;
import com.topcoder.scraper.service.WebpageService;

import lombok.Getter;
import lombok.Setter;


/**
 * Amazon implementation of AuthenticationCrawler
 */
@Component
public class AmazonAuthenticationCrawler extends AbstractAuthenticationCrawler {

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

    public AmazonAuthenticationCrawler(AmazonProperty property, WebpageService webpageService) {
        this.siteName = "amazon";
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
    @Override
    public AmazonAuthenticationCrawlerResult authenticate(TrafficWebClient webClient,
                                                          String username, String password, String code,
                                                          boolean init) throws IOException {
        if (homePage == null) {
            webClient.getWebClient().getCookieManager().clearCookies();
            // Fetch homepage
            homePage = webClient.getPage(property.getUrl());
            // Save page
            webpageService.save("home", siteName, homePage.getWebResponse().getContentAsString());
        }

        SubmitResult result = null;
        if (authStep == AuthStep.FIRST) {
            logger.info("start auth step 1");
            // Check CAPTCHA 1st
            HtmlInput captchaInput1st = homePage.querySelector(property.getCrawling().getLoginPage().getCaptchaInput1st());
            if (captchaInput1st != null) {
                String hackImageForStep1 = "data:image/png;base64,";
                if (code == null) { // return image to client
                    LOGGER.info("Captcha 1st Input Found, at step = " + authStep);
                    HtmlImage image = homePage.querySelector(property.getCrawling().getLoginPage().getCaptchaImage1st());
                    webpageService.saveImage("Captcha-01", "png", siteName, image);
                    return new AmazonAuthenticationCrawlerResult(false,
                            "CAPTCHA code needed on home page", CodeType.CAPTCHA, hackImageForStep1 + webpageService.toBase64Image(image), true);
                } else { // fill code
                    result = handleCaptchaInput1st(webClient, homePage, code);
                    homePage = webClient.getPage(property.getUrl());
                    if (result.isSuccess()) {     // succeed
                        authStep = AuthStep.SECOND; // goto step2
                        code = null; // set code to null
                    } else { // failed, return code again
                        HtmlImage image = homePage.querySelector(property.getCrawling().getLoginPage().getCaptchaImage1st());
                        return new AmazonAuthenticationCrawlerResult(false,
                                "CAPTCHA code needed on home page", CodeType.CAPTCHA, hackImageForStep1 + webpageService.toBase64Image(image), true);
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

                if (homePage == null) {
                    logger.info("why homePage is null ??");
                }
                // click login button
                HtmlPage loginPage = webClient.click(homePage.querySelector(property.getCrawling().getLoginButton()));

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

                finalPage = submitLogin(webClient, loginPage, username, password);

                // Save page
                webpageService.save("login-click", siteName, finalPage.getWebResponse().getContentAsString());

                // Check Login Successfully > CAPTCHA
        /*
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
        */
                HtmlPasswordInput passwordInputCheck = finalPage.querySelector(property.getCrawling().getLoginPage().getPasswordInput());
                if (passwordInputCheck != null) {
                    // still in login page
                    HtmlTextInput captchaInput = finalPage.querySelector(property.getCrawling().getLoginPage().getCaptchaInput2nd());
                    if (captchaInput != null) {
                        LOGGER.info("Captcha 2nd Input Found, at step " + authStep);
                        HtmlImage htmlImg = finalPage.querySelector(property.getCrawling().getLoginPage().getCaptchaImage2nd());
                        return new AmazonAuthenticationCrawlerResult(false,
                                "CAPTCHA code needed on login page", CodeType.CAPTCHA, webpageService.toBase64Image(htmlImg), true);
                    } else {
//                        authStep = AuthStep.LAST; // goto last step
                        LOGGER.info("Incorrect Password, at step " + authStep);
                        return new AmazonAuthenticationCrawlerResult(false, "Incorrect Password", null, null, true);
                    }
                } else {
                    authStep = AuthStep.LAST; // goto last step
                }
            } else { // fill code
                logger.info("start check Captcha in step 2 with code = " + code);
                result = handleCaptchaInput2st(webClient, finalPage, password, code);
                finalPage = result.getHtmlPage();

                if (result.isSuccess()) {
                    path = result.getHtmlPath();
                    authStep = AuthStep.LAST; // goto last step
                    code = null; // and clear code
                } else { // failed
                    logger.info("check Captcha in step2 failed, now return error to frontend again");

                    HtmlPasswordInput passwordInput = finalPage.querySelector(property.getCrawling().getLoginPage().getPasswordInput());
                    if (passwordInput != null) {
                        finalPage = submitLogin(webClient, finalPage, username, password);
                    }

                    HtmlImage htmlImg = finalPage.querySelector(property.getCrawling().getLoginPage().getCaptchaImage2nd());
                    if (htmlImg != null) {
                        return new AmazonAuthenticationCrawlerResult(false,
                                "CAPTCHA code needed on Login page", CodeType.CAPTCHA, webpageService.toBase64Image(htmlImg), true);
                    }
                }
            }
        }

        if (authStep == AuthStep.LAST) {
            if (code == null) {
                // need verification code
                HtmlSubmitInput sendCode = finalPage.querySelector(property.getCrawling().getLoginPage().getContinueInput());
                // continue button is optional, it shows sometimes
                if (sendCode != null) {
                    finalPage = webClient.click(sendCode);
                    webpageService.save("verification-code", siteName, finalPage.getWebResponse().getContentAsString());
                    return new AmazonAuthenticationCrawlerResult(false, "Email Verification Code Needed, check your email",
                            CodeType.Verification, null, true);
                }

                // Check Login Successfully > MFA Code Needed
                HtmlTelInput mfaInputCheck = finalPage.querySelector(property.getCrawling().getLoginPage().getMfaInput());
                if (mfaInputCheck != null) {
                    LOGGER.info("MFA Code Needed, at step = " + authStep);
                    return new AmazonAuthenticationCrawlerResult(false, "MFA Code Needed",
                            CodeType.MFA, null, true);
                }

                // Check Login Successfully > Verification Code Needed
                HtmlRadioButtonInput smsInputCheck = finalPage.querySelector("input[type='radio'][name='option']");
                if (smsInputCheck != null) {
                    LOGGER.info("Verification Code Needed, at step = " + authStep);
                    return new AmazonAuthenticationCrawlerResult(false, "Verification Code Needed",
                            CodeType.Verification, null, true);
                }

                AmazonAuthenticationCrawlerResult amazonAuthenticationCrawlerResult = CheckSmsApprovalStatus();
                if (amazonAuthenticationCrawlerResult != null) return amazonAuthenticationCrawlerResult;

            } else {

                // verification code input
                HtmlTextInput codeInput = finalPage.querySelector(property.getCrawling().getLoginPage().getVerificationCodeInput());
                HtmlSubmitInput sendCode = finalPage.querySelector(property.getCrawling().getLoginPage().getVerificationCodeSubmit());
                HtmlSubmitInput mfaLoginButton = finalPage.querySelector(property.getCrawling().getLoginPage().getMfaLoginButton());
                HtmlSpan smsSpan = finalPage.querySelector(property.getCrawling().getLoginPage().getSmsApproval());

                if (smsSpan != null) {
                    finalPage = (HtmlPage) finalPage.refresh();
                    AmazonAuthenticationCrawlerResult amazonAuthenticationCrawlerResult = CheckSmsApprovalStatus();
                    if (amazonAuthenticationCrawlerResult != null) return amazonAuthenticationCrawlerResult;
                } else if (codeInput != null && sendCode != null) {
                    codeInput.type(code);
                    finalPage = webClient.click(sendCode);
                    String path = webpageService.save("enter-verification-code", siteName, finalPage.getWebResponse().getContentAsString());

                    // still exist
                    if (finalPage.querySelector(property.getCrawling().getLoginPage().getVerificationCodeSubmit()) != null) {
                        return new AmazonAuthenticationCrawlerResult(false, "Code error, Verification Code Needed",
                                CodeType.Verification, null, true);
                    }
                    result = new SubmitResult(true, finalPage, path);
                    authStep = AuthStep.DONE;
                } else if (mfaLoginButton != null) {
                    result = this.fillMFACodeNeeded(webClient, finalPage, code);
                    if (result != null && result.isSuccess()) {
                        authStep = AuthStep.DONE;
                    } else {
                        LOGGER.info("MFA Code check failed, at step = " + authStep);
                        // here MFA code check failed, we cannot continue
                        return new AmazonAuthenticationCrawlerResult(false, "MFA Code Needed",
                                CodeType.MFA, null, true);
                    }
                } else {
                    result = this.fillVerificationCodeNeeded(webClient, finalPage, code);
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
        }

        if (result == null || result.getHtmlPage() == null) {
            result = new SubmitResult(true, finalPage, "");
        }
        if (authStep == AuthStep.DONE) {
            // Save page
            webpageService.save("login-done", siteName, result.getHtmlPage().getWebResponse().getContentAsString());
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
    @Override
    public AmazonAuthenticationCrawlerResult authenticate(TrafficWebClient webClient,
                                                          String username, String password, String initCode // fixme: initCode -> xxxCode
    ) throws IOException {
        throw new UnsupportedOperationException();
    }

    private HtmlPage submitLogin(TrafficWebClient webClient, HtmlPage page,
                                 String username, String password) throws IOException {

        // Fill in password
        //HtmlPasswordInput passwordInput = passwordPage.getFirstByXPath("//input[@id=\"ap_password\"]");
        HtmlPasswordInput passwordInput = page.querySelector(property.getCrawling().getLoginPage().getPasswordInput());
        passwordInput.type(password);

        // Fill in remember-me
        // *Experimental Code*
        HtmlCheckBoxInput rememberMeCheckInput = page.querySelector("input[type='checkbox'][name='rememberMe']");
        rememberMeCheckInput.setChecked(true);
        LOGGER.info("Remember Me Check:" + rememberMeCheckInput.isChecked());

        // Submit form
        HtmlSubmitInput submitInput2 = page.querySelector(property.getCrawling().getLoginPage().getSubmitButton());
        HtmlPage result = webClient.click(submitInput2);

        // Save page
        webpageService.save("login-click", siteName, result.getWebResponse().getContentAsString());

        return result;
    }

    private SubmitResult handleCaptchaInput1st(TrafficWebClient webClient, HtmlPage page, String code) throws IOException {
        // Fill in captcha
        HtmlTextInput captchaInput = page.querySelector(property.getCrawling().getLoginPage().getCaptchaInput1st());
        captchaInput.type(code);

        // Submit form
        HtmlButton submit = page.querySelector(property.getCrawling().getLoginPage().getCaptchaSubmit1st());
        HtmlPage page2 = webClient.click(submit);

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

    private SubmitResult handleCaptchaInput2st(TrafficWebClient webClient, HtmlPage page, String password, String code) throws IOException {

        // Fill in password
        HtmlPasswordInput passwordInput = page.querySelector(property.getCrawling().getLoginPage().getPasswordInput());
        passwordInput.type(password);

        // Fill in captcha
        HtmlTextInput captchaInput = page.querySelector(property.getCrawling().getLoginPage().getCaptchaInput2nd());
        captchaInput.type(code);

        // Submit form
        HtmlSubmitInput submitInput = page.querySelector(property.getCrawling().getLoginPage().getSubmitButton());
        HtmlPage page2 = webClient.click(submitInput);

        // Save page
        String path = webpageService.save("login-captcha2nd", siteName, page2.getWebResponse().getContentAsString());

        // captcha input check
        HtmlPasswordInput passwordInput2 = page2.querySelector(property.getCrawling().getLoginPage().getPasswordInput());
        if (passwordInput2 == null) {
            // success case
            return new SubmitResult(true, page2, path);
        } else {
            return new SubmitResult(false, page2, path);
        }
    }

    private SubmitResult fillMFACodeNeeded(TrafficWebClient webClient, HtmlPage page, String code) throws IOException {

        // Fill in MFA Code
        HtmlTelInput codeInput = page.querySelector(property.getCrawling().getLoginPage().getMfaInput());
        codeInput.type(code);

        // Submit form
        // Continue Submit form
        HtmlSubmitInput mfaLoginButton = page.querySelector(property.getCrawling().getLoginPage().getMfaLoginButton());
        HtmlPage page1 = webClient.click(mfaLoginButton);

        // Save page
        String path1 = webpageService.save("login-mfa-submit", siteName, page1.getWebResponse().getContentAsString());

        // code input check
        HtmlTelInput codeInputCheck = page1.querySelector(property.getCrawling().getLoginPage().getMfaInput());
        if (codeInputCheck == null) {
            // success case
            return new SubmitResult(true, page1, path1);
        } else {
            return null;
        }
    }

    private SubmitResult fillVerificationCodeNeeded(TrafficWebClient webClient, HtmlPage page, String code) throws IOException {

        // Continue Submit form
        HtmlSubmitInput continueSubmitInput1 = page.querySelector(property.getCrawling().getLoginPage().getContinueInput());
        HtmlPage page1 = webClient.click(continueSubmitInput1);

        // Fill in Verification Code
        HtmlTextInput codeInput = page1.querySelector("input[type='text'][name='code']");
        codeInput.type(code);

        // Submit form
        // TODO: select correct selector and delete others
        HtmlSubmitInput continueSubmitInput2 = page1.querySelector("#a-autoid-0 > span > input");
        if (continueSubmitInput2 == null) continueSubmitInput2 = page1.querySelector("span#a-autoid-0 > span > input");
        if (continueSubmitInput2 == null) continueSubmitInput2 = page1.querySelector("input[type='submit']");
        HtmlPage page2 = webClient.click(continueSubmitInput2);

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

    private String readCodeFromFile(String prompt) throws IOException {
        File codeInputFile = new File(CODE_PATH);
        int waitCount = CODE_WAIT_COUNT_MAX;

        while (!codeInputFile.exists() && waitCount > 0) {
            LOGGER.info("Please write " + prompt + " in " + CODE_PATH + ". sleep " + CODE_WAIT_MILLI_SEC + " milliseconds, count:" + waitCount);
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

    private AmazonAuthenticationCrawlerResult CheckSmsApprovalStatus() {

        HtmlSpan smsSpan = finalPage.querySelector(property.getCrawling().getLoginPage().getSmsApproval());
        if (smsSpan != null) {
            LOGGER.info("SMS Approval Needed, at step = " + authStep);
            return new AmazonAuthenticationCrawlerResult(false, "SMS Approval Needed",
                    CodeType.SMSApproval, null, true);
        }

        HtmlAnchor orderButton = finalPage.querySelector(property.getCrawling().getHomePage().getOrdersButton());
        if (orderButton == null) {
            authStep = AuthStep.ERROR;
            return new AmazonAuthenticationCrawlerResult(false,
                    "Landed unexpected page. Unable to proceed anymore.",
                    null, null, false);
        }

        authStep = AuthStep.DONE;
        return null;
    }
}
