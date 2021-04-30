package com.topcoder.scraper.module.ecunifiedmodule;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.IBasicModule;
import com.topcoder.scraper.service.WebpageService;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Component
public class TestScrapingModule implements IBasicModule {

    /**
     * logger instance
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    List<String> urls = new ArrayList<>();

    @Autowired
    private WebpageService webpageService;

    public void run(ApplicationArguments args) {

//        urls.add("https://amiunique.org/fp");
//        urls.add("https://firstpartysimulator.org/kcarter?aat=1");
//        urls.add("https://antoinevastel.com/bots/datadome");
//        urls.add("https://antoinevastel.com/bots/");

        urls.add("https://item.rakuten.co.jp/bookfan/bk-4120040879");
        urls.add("https://item.rakuten.co.jp/compmoto-r/9999999999550");
        urls.add("https://item.rakuten.co.jp/gourmetcoffee/2624");
        urls.add("https://item.rakuten.co.jp/maxshare/a11846");
        urls.add("https://item.rakuten.co.jp/yvetteonlineshop/080160");
        urls.add("https://item.rakuten.co.jp/enetroom/569938-com");
        urls.add("https://item.rakuten.co.jp/f406104-fukuchi/f99-03");
        urls.add("https://item.rakuten.co.jp/f462039-kanoya/1361");
        urls.add("https://item.rakuten.co.jp/f402290-miyama/b15");
        urls.add("https://item.rakuten.co.jp/f452025-miyakonojo/mj-8404");
        urls.add("https://item.rakuten.co.jp/f015814-atsuma/1018");
        urls.add("https://item.rakuten.co.jp/gourmetcoffee/3204_200g");
        urls.add("https://item.rakuten.co.jp/spg-sports/snw-stw150");
        urls.add("https://item.rakuten.co.jp/tavola/59700303-a01ta");
        urls.add("https://item.rakuten.co.jp/gourmetcoffee/0403-20s");
        urls.add("https://item.rakuten.co.jp/premium-deli/tonkotsu");
        urls.add("https://item.rakuten.co.jp/futon-colors/10000053");
        urls.add("https://item.rakuten.co.jp/tansu/10119003");
        urls.add("https://item.rakuten.co.jp/rakuten24/88483");
        urls.add("https://item.rakuten.co.jp/kk-shimaya/1022");
        urls.add("https://item.rakuten.co.jp/trust-rady/tl125");
        urls.add("https://item.rakuten.co.jp/sizemarusye/koganesizesatu");
        urls.add("https://item.rakuten.co.jp/auc-kurashi-kaientai/1043008-oma5kg");

        remoteGrid();
//        chrome(true);
//        chrome(false);

//        htmlUnit();
    }

    public void htmlUnit() {

        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36";
        BrowserVersion browserVersion = new BrowserVersion
                .BrowserVersionBuilder(BrowserVersion.CHROME).setUserAgent(userAgent).build();
        WebClient webClient = new WebClient(browserVersion);

        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setJavaScriptEnabled(false);

        DefaultCredentialsProvider scp = new DefaultCredentialsProvider();
        scp.addCredentials("lum-customer-c_5ae15880-zone-unblocker1", "al1otnykj38l", "zproxy.lum-superproxy.io", 22225, null);
        webClient.setCredentialsProvider(scp);

        for (String url: urls) {
            sleep();
            HtmlPage page = null;
            try {
                page = webClient.getPage(url);
                logger.debug(page.getTitleText());
                savePage("htmlunit", "Test", page.getWebResponse().getContentAsString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            webClient.getCookieManager().clearCookies();
         }
    }

    public void remoteGrid() {
        ChromeOptions chromeOptions = new ChromeOptions();

        Proxy proxy = new Proxy();
        proxy.setHttpProxy("http://lum-customer-c_5ae15880-zone-unblocker1:al1otnykj38l@zproxy.lum-superproxy.io:22225");
        chromeOptions.setProxy(proxy);

        WebDriver driver = null;
        try {
            driver = new RemoteWebDriver(new URL("http://localhost:4444"), chromeOptions);

            for (String url: urls) {
                sleep();
                driver.get(url);

                logger.debug(driver.getTitle());
                savePage("selenium-grid", "Test", driver.getPageSource());

                driver.manage().deleteAllCookies();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            if (driver != null) driver.quit();
        }
    }

    public void chrome(boolean isHeadless) {

//        String chromeDriverPath = "/usr/local/bin/chromedriver" ;
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        System.setProperty("webdriver.chrome.logfile", "/Users/tetsuo.shioda/Yodobashi/log/chromedriver.log");
        System.setProperty("webdriver.chrome.verboseLogging", "true");

        ChromeOptions options = new ChromeOptions();
        if (isHeadless) {
            options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
            options.setHeadless(true);
        }

        DesiredCapabilities cap = DesiredCapabilities.chrome();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        cap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

//        // Add the WebDriver proxy capability.
        Proxy proxy = new Proxy();
        proxy.setHttpProxy("http://lum-customer-c_5ae15880-zone-unblocker1:al1otnykj38l@zproxy.lum-superproxy.io:22225");
        options.setProxy(proxy);

        WebDriver driver = new ChromeDriver(options);

        for (String url: urls) {
            sleep();
            driver.get(url);
            logger.debug(driver.getTitle());

//            LogEntries logEntries = driver.manage().logs().get("performance");
//            logEntries.forEach(l -> {logger.debug(l.toString());});

            driver.manage().deleteAllCookies();
            savePage(isHeadless ? "chrome-headless" : "chrome", "Test", driver.getPageSource());
        }
    }

    private void sleep() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String savePage(String fileName, String siteName, String contents) {
        //Characters that cannot be used in folder names are replaced as underscore
        fileName = fileName.replaceAll("[/><?:\"\\*|;]", "_");
        // save html page
        String saveContents = convertHtmlCharset(contents);
        saveContents = convertToAbsolutePath(saveContents);
        return webpageService.save(fileName, siteName, saveContents, true);
    }

    private String convertHtmlCharset(String contents) {
        return contents.replace("charset=euc-jp", "charset=utf-8")
                .replace("charset=EUC-JP", "charset=utf-8");
    }

    private String convertToAbsolutePath(String contents) {
        return contents.replace("src=\"//", "src=\"https://")
                .replace("src='//", "src='https://")
                .replace("href=\"//", "href=\"https://")
                .replace("href='//", "href='https://");
    }

    @Override
    public String getModuleType() {
        return "test";
    }
}
