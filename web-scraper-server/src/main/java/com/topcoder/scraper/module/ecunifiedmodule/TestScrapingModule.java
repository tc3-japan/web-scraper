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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TestScrapingModule implements IBasicModule {

    private class ThreadHaltException extends Exception {}

    @Service
    private class AsyncService {

        public AsyncService() {
        }

        public void test() throws InterruptedException, MalformedURLException {

//            String proxyString = "http://zproxy.lum-superproxy.io:22225";
            String proxyString = "zproxy.lum-superproxy.io:22225";
//            String proxyString = "lum-customer-c_5ae15880-zone-ds_ex-ip-176.105.248.32:pq1gt7si9gcm@zproxy.lum-superproxy.io:22225";
//            String proxyString = "http://lum-customer-c_5ae15880-zone-ds_ex-ip-176.105.248.32:pq1gt7si9gcm@zproxy.lum-superproxy.io:22225";
//            String proxyString = "localhost:8081";
//            String proxyString = "http://52.194.250.99:60088";

            ChromeOptions chromeOptions = new ChromeOptions();
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            Proxy proxy = new Proxy();
            proxy.setProxyType(Proxy.ProxyType.MANUAL)
                    .setAutodetect(false)
                    .setHttpProxy(proxyString)
                    .setSslProxy(proxyString);
            chromeOptions.setProxy(proxy);
            firefoxOptions.setProxy(proxy);

            WebDriver driver = new RemoteWebDriver(new URL("http://localhost:4445"), firefoxOptions);
//            WebDriver driver = new RemoteWebDriver(new URL("http://localhost:4444"), chromeOptions);
            driver.get("https://lumtest.com/myip.json");
//            Thread.sleep(5000);
//            driver.wait();
            Alert alert = driver.switchTo().alert();
            alert.sendKeys("lum-customer-c_5ae15880-zone-ds_ex-ip-176.105.248.32");
            alert.sendKeys(Keys.TAB.toString());
            alert.sendKeys( "pq1gt7si9gcm");
            alert.accept();

            logger.debug(driver.getPageSource());
        }
        public AsyncService() {}

        @Async
        public void scrape(String search, Pattern pattern, String proxyString, int inc) {

            ChromeOptions chromeOptions = new ChromeOptions();

            Proxy proxy = new Proxy();
            proxy.setHttpProxy(proxyString);
            chromeOptions.setProxy(proxy);
            logger.debug(String.format("Proxy : %s", proxyString));

            WebDriver driver = null;
            try {
                driver = new RemoteWebDriver(new URL("http://localhost:4444"), chromeOptions);
                int count = 0;

                do {
                    for (String keyword : keywords) {

                        for (int j = 1; j <= 10 * inc; j += inc) {
                            String search_ = String.format(search, keyword, j);

                            sleep();
                            driver.manage().deleteAllCookies();
                            driver.get(search_);
                            logger.debug(String.format("Search : %s", search_));

                            Set<String> links = new HashSet<>();
                            for (WebElement a : driver.findElements(By.tagName("a"))) {
                                String link = a.getAttribute("href");
                                if (link == null) continue;

                                Matcher matcher = pattern.matcher(link);
                                if (matcher.matches()) {
                                    links.add(matcher.group());
                                }
                            }

                            if (links.isEmpty()) break;

                            for (String link : links) {
                                sleep();
                                driver.manage().deleteAllCookies();
                                driver.get(link);
                                logger.debug(String.format("Count:%d, Title:%s", count++, driver.getTitle()));

                                if (halt) {
                                    logger.debug("Scraping Halt");
                                    throw new ThreadHaltException();
                                }
                            }
                        }
                    }
                } while (true);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ThreadHaltException e) {
            } finally {
                if (driver != null) driver.quit();
            }
        }

    }

    /**
     * logger instance
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final List<String> urls = new ArrayList<>();
    private final static long maxMin = 10;
    private boolean halt = false;

    private final List<String> keywords = new ArrayList<>();
    private final List<String> proxies = new ArrayList<>();

    @Autowired
    private WebpageService webpageService;

    @Autowired
    private AsyncService asyncService;

    public TestScrapingModule() {
        keywords.add("野菜");
        keywords.add("肉");
        keywords.add("魚");
        keywords.add("酒");
        keywords.add("レディース");
        keywords.add("メンズ");
        keywords.add("靴");
        keywords.add("ビジネス");
        keywords.add("漫画");
        keywords.add("ゲーム");

        proxies.add("http://52.194.250.99:60088");
//        proxies.add("http://54.150.254.214:60088");
//        proxies.add("http://54.168.178.35:60088");
//        proxies.add("http://lum-customer-c_5ae15880-zone-ds_ex-ip-176.105.248.32:pq1gt7si9gcm@zproxy.lum-superproxy.io:22225");
//        proxies.add("http://lum-customer-c_5ae15880-zone-ds_ex-ip-176.105.250.93:pq1gt7si9gcm@zproxy.lum-superproxy.io:22225");
//        proxies.add("http://lum-customer-c_5ae15880-zone-ds_ex-ip-195.234.89.242:pq1gt7si9gcm@zproxy.lum-superproxy.io:22225");
//        proxies.add("http://lum-customer-c_5ae15880-zone-ds_ex-ip-92.249.32.103:pq1gt7si9gcm@zproxy.lum-superproxy.io:22225");
//        proxies.add("http://lum-customer-c_5ae15880-zone-ds_ex-ip-195.234.88.202:pq1gt7si9gcm@zproxy.lum-superproxy.io:22225");
//        proxies.add("http://lum-customer-c_5ae15880-zone-ds_ex-ip-194.110.88.99:pq1gt7si9gcm@zproxy.lum-superproxy.io:22225");
//        proxies.add("http://lum-customer-c_5ae15880-zone-ds_ex-ip-43.225.80.250:pq1gt7si9gcm@zproxy.lum-superproxy.io:22225");
//        proxies.add("http://lum-customer-c_5ae15880-zone-ds_ex-ip-43.225.80.224:pq1gt7si9gcm@zproxy.lum-superproxy.io:22225");
//        proxies.add("http://lum-customer-c_5ae15880-zone-ds_ex-ip-194.110.91.188:pq1gt7si9gcm@zproxy.lum-superproxy.io:22225");
//        proxies.add("http://lum-customer-c_5ae15880-zone-ds_ex-ip-194.53.189.253:pq1gt7si9gcm@zproxy.lum-superproxy.io:22225");
    }

    public void run(ApplicationArguments args) {

        String a_search = "https://www.amazon.co.jp/s?k=%s&page=%d";
        Pattern a_pattern = Pattern.compile("https:\\/\\/www\\.amazon\\.co\\.jp\\/.+?(\\/dp\\/.*?\\/)ref=.*");

        String r_search = "https://search.rakuten.co.jp/search/mall/%s/?p=%d";
        Pattern r_pattern = Pattern.compile("https:\\/\\/item\\.rakuten\\.co\\.jp\\/.*?\\/.*?\\/");

        String y_search = "https://shopping.yahoo.co.jp/search?p=%s&b=%d";
        Pattern y_pattern = Pattern.compile("(https:\\/\\/store\\.shopping\\.yahoo\\.co\\.jp\\/.*?\\/.*?)");

//        products(a_search, a_pattern, null, 1);

        Date start = new Date();
        halt = false;

//        try {
//            asyncService.test();
//            if (true) return;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }

        for (String proxy : proxies) {
//            asyncService.scrape(a_search, a_pattern, proxy, 1);
//            asyncService.scrape(r_search, r_pattern, proxy, 1);
            asyncService.scrape(y_search, y_pattern, proxy, 30);
        }

        try {
            do {
                long min = ((new Date()).getTime() - start.getTime()) / (60 * 1000) % 60;
                if (min >= maxMin) {
                    halt = true;
                    break;
                }
                Thread.sleep(60000);
            } while (true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        urls.add("https://amiunique.org/fp");
//        urls.add("https://firstpartysimulator.org/kcarter?aat=1");
//        urls.add("https://antoinevastel.com/bots/datadome");
//        urls.add("https://antoinevastel.com/bots/");

//        urls.add("https://item.rakuten.co.jp/auc-kurashi-kaientai/1043008-oma5kg");
//        urls.add("https://item.rakuten.co.jp/bookfan/bk-4120040879");
//        urls.add("https://item.rakuten.co.jp/compmoto-r/9999999999550");
//        urls.add("https://item.rakuten.co.jp/gourmetcoffee/2624");
//        urls.add("https://item.rakuten.co.jp/maxshare/a11846");
//        urls.add("https://item.rakuten.co.jp/yvetteonlineshop/080160");
//        urls.add("https://item.rakuten.co.jp/enetroom/569938-com");
//        urls.add("https://item.rakuten.co.jp/f406104-fukuchi/f99-03");
//        urls.add("https://item.rakuten.co.jp/f462039-kanoya/1361");
//        urls.add("https://item.rakuten.co.jp/f402290-miyama/b15");
//        urls.add("https://item.rakuten.co.jp/f452025-miyakonojo/mj-8404");
//        urls.add("https://item.rakuten.co.jp/f015814-atsuma/1018");
//        urls.add("https://item.rakuten.co.jp/gourmetcoffee/3204_200g");
//        urls.add("https://item.rakuten.co.jp/spg-sports/snw-stw150");
//        urls.add("https://item.rakuten.co.jp/tavola/59700303-a01ta");
//        urls.add("https://item.rakuten.co.jp/gourmetcoffee/0403-20s");
//        urls.add("https://item.rakuten.co.jp/premium-deli/tonkotsu");
//        urls.add("https://item.rakuten.co.jp/futon-colors/10000053");
//        urls.add("https://item.rakuten.co.jp/tansu/10119003");
//        urls.add("https://item.rakuten.co.jp/rakuten24/88483");
//        urls.add("https://item.rakuten.co.jp/kk-shimaya/1022");
//        urls.add("https://item.rakuten.co.jp/trust-rady/tl125");
//        urls.add("https://item.rakuten.co.jp/sizemarusye/koganesizesatu");

//        remoteGrid();

//        chrome(true);
//        chrome(false);
//        htmlUnit();
    }

    private void htmlUnit() {

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

        for (String url : urls) {
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

    private void remoteGrid() {
        ChromeOptions chromeOptions = new ChromeOptions();

        Proxy proxy = new Proxy();
        proxy.setHttpProxy("http://lum-customer-c_5ae15880-zone-unblocker1:al1otnykj38l@zproxy.lum-superproxy.io:22225");
        chromeOptions.setProxy(proxy);

        WebDriver driver = null;
        try {
            driver = new RemoteWebDriver(new URL("http://localhost:4444"), chromeOptions);

            for (String url : urls) {
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

    private void chrome(boolean isHeadless) {

//        String chromeDriverPath = "/usr/local/bin/chromedriver" ;
//        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        System.setProperty("webdriver.chrome.logfile", "/Users/tetsuo.shioda/Yodobashi/log/chromedriver.log");
        System.setProperty("webdriver.chrome.verboseLogging", "true");

        ChromeOptions options = new ChromeOptions();
        if (isHeadless) {
            options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
            options.setHeadless(true);
        }

//        DesiredCapabilities cap = DesiredCapabilities.chrome();
//        LoggingPreferences logPrefs = new LoggingPreferences();
//        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
//        cap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);

//        // Add the WebDriver proxy capability.
        Proxy proxy = new Proxy();
        proxy.setHttpProxy("http://lum-customer-c_5ae15880-zone-unblocker1:al1otnykj38l@zproxy.lum-superproxy.io:22225");
        options.setProxy(proxy);

        WebDriver driver = new ChromeDriver(options);

        for (String url : urls) {
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
