package com.topcoder.scraper.module.ecunifiedmodule;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Component
public class TestScrapingModule implements IBasicModule {

    /**
     * logger instance
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WebpageService webpageService;

    public void run(ApplicationArguments args) {

//        String chromeDriverPath = "/usr/local/bin/chromedriver" ;
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        System.setProperty("webdriver.chrome.logfile", "/Users/tetsuo.shioda/Yodobashi/log/chromedriver.log");
        System.setProperty("webdriver.chrome.verboseLogging", "true");

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");
//        options.setHeadless(true);

//        LoggingPreferences logPrefs = new LoggingPreferences();
//        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
//        options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        cap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

//        // Add the WebDriver proxy capability.
//        Proxy proxy = new Proxy();
//        proxy.setHttpProxy("54.238.193.77:60088");
//        options.setProxy(proxy);

        WebDriver driver = new ChromeDriver(options);
//        driver.get("https://www.google.com");
//        logger.debug(driver.getTitle());

        List<String> products = new ArrayList<>();
        products.add("bookfan/bk-4120040879");
        products.add("compmoto-r/9999999999550");
        products.add("gourmetcoffee/2624");
        products.add("maxshare/a11846");
        products.add("yvetteonlineshop/080160");
        products.add("enetroom/569938-com");
        products.add("f406104-fukuchi/f99-03");
        products.add("f462039-kanoya/1361");
        products.add("f402290-miyama/b15");
        products.add("f452025-miyakonojo/mj-8404");
        products.add("f015814-atsuma/1018");
        products.add("gourmetcoffee/3204_200g");
        products.add("spg-sports/snw-stw150");
        products.add("tavola/59700303-a01ta");
        products.add("gourmetcoffee/0403-20s");
        products.add("premium-deli/tonkotsu");
        products.add("futon-colors/10000053");
        products.add("tansu/10119003");
        products.add("rakuten24/88483");
        products.add("kk-shimaya/1022");
        products.add("trust-rady/tl125");
        products.add("sizemarusye/koganesizesatu");
        products.add("auc-kurashi-kaientai/1043008-oma5kg");

        for (String path: products) {
            sleep();
            driver.get("https://item.rakuten.co.jp/" + path);
            logger.debug(driver.getTitle());

//            LogEntries logEntries = driver.manage().logs().get("performance");
//            logEntries.forEach(l -> {logger.debug(l.toString());});

            driver.manage().deleteAllCookies();
//            savePage("rakuten", "Test", driver.getPageSource());
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
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
