package com.topcoder.scraper.module.ecisolatedmodule.rakuten.crawler;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.lib.navpage.NavigableAuthenticationPage;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractAuthenticationCrawler;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Rakuten implementation of AuthenticationCrawler
 */
public class RakutenAuthenticationCrawler extends AbstractAuthenticationCrawler {

    private Logger logger = LoggerFactory.getLogger(RakutenAuthenticationCrawler.class.getName());

    private String siteName;

    private final WebpageService webpageService;

    public RakutenAuthenticationCrawler(String siteName, WebpageService webpageService) {
        this.siteName = siteName;
        this.webpageService = webpageService;
    }

    @Override
    public RakutenAuthenticationCrawlerResult authenticate(TrafficWebClient webClient,
                                                           String username,
                                                           String password, String code, boolean init) throws IOException {
        return new RakutenAuthenticationCrawlerResult(false, null);
    }

    @Override
    public RakutenAuthenticationCrawlerResult authenticate(TrafficWebClient webClient, String username, String password, String code) throws IOException {
        webClient.getWebClient().getCookieManager().clearCookies();
        webClient.getWebClient().getOptions().setJavaScriptEnabled(true);

        HtmlPage page = webClient.getPage("https://grp01.id.rakuten.co.jp/rms/nid/vc?__event=login&service_id=top");
        NavigableAuthenticationPage authPage = new NavigableAuthenticationPage(page, webClient);

        authPage.savePage("rakuten-auth", this.siteName, webpageService);

        authPage.type(username, "#loginInner_u");
        authPage.typePassword(password, "#loginInner_p");
        authPage.typeCheckbox("off", "#auto_logout");
        authPage.click(".loginButton", webpageService);

        authPage.savePage("rakuten-authenticated", siteName, webpageService);

        authPage.setPage("https://order.my.rakuten.co.jp/");
        authPage.savePage("rakuten-order", siteName, webpageService);

        authPage.confirmLoginByElementExists("h2.ri-cmn-hdr-unique-ttl.gs_copied", ".oDrListItem:nth-child(1) .purchaseDate");

        return new RakutenAuthenticationCrawlerResult(authPage.getLoginStatus(), null);
    }
}
