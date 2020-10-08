package com.topcoder.scraper.lib.navpage;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.traffic.TrafficWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigableAuthenticationPage extends NavigablePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigableAuthenticationPage.class.getName());

    private boolean loggedIn;

    public NavigableAuthenticationPage(HtmlPage page, TrafficWebClient webClient) {
        super(page, webClient);
        loggedIn = false;
    }

    public NavigableAuthenticationPage(String url, TrafficWebClient webClient) {
        super(url, webClient);
        loggedIn = false;
    }

    public boolean confirmLoginByElementExists(String... selectors) {

        HtmlElement img = null;
        for (String selector: selectors) {
            img = getPage().querySelector(selector);
            if (img != null) break;
        }

        boolean loginSuccess;
        if (img != null) {
            LOGGER.info("Logged in successfully");
            loginSuccess = true;
        } else {
            LOGGER.info("Failed to login");
            loginSuccess = false;
        }

        loggedIn = loginSuccess;

        return loginSuccess;
    }

    public boolean confirmLoginByMissingLoginText(String selector, String text) {

        HtmlElement loginNode = getPage().querySelector(selector);
        boolean matchResult = loginNode.getTextContent().matches(".*" + text + ".*");
        boolean loginSuccess;
        if (!matchResult) {
            LOGGER.info("Logged in successfully");
            loginSuccess = true;
        } else {
            LOGGER.info("Failed to login");
            loginSuccess = false;
        }
        loggedIn = loginSuccess;
        return loginSuccess;
    }

    public boolean getLoginStatus() {
        return loggedIn;
    }
}
