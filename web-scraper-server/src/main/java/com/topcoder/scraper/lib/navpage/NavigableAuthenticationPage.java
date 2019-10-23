package com.topcoder.scraper.lib.navpage;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.traffic.TrafficWebClient;

public class NavigableAuthenticationPage extends NavigablePage {

    // TrafficWebClient webClient;
    // HtmlPage page;
    private boolean loggedIn;

    public NavigableAuthenticationPage(HtmlPage page, TrafficWebClient webClient) {
        super(page, webClient);
        loggedIn = false;
    }

    public NavigableAuthenticationPage(String url, TrafficWebClient webClient) {
        super(url, webClient);
        loggedIn = false;
    }

    public boolean ConfirmLoginByElementExists(String selector) {
        // If we see the yahoo image, consider ourselves logged in
        HtmlElement yahooImg = getPage()
                .querySelector(selector);
        //savePage("yahoo-authenticated", siteName, webpageService);

        boolean loginSuccess;
        if (yahooImg != null) {
            System.out.println("Logged in successfully");
            loginSuccess = true;
        } else {
            System.out.println("Failed to login");
            loginSuccess = false;
        }

        loggedIn = loginSuccess;

        return loginSuccess;
    }

    public boolean getLoginStatus() {
        return loggedIn;
    }
}
