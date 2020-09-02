package com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler;

import com.topcoder.common.model.CodeType;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractAuthenticationCrawlerResult;

/**
 * Result from YahooAuthenticationCrawler
 */
public class YahooAuthenticationCrawlerResult extends AbstractAuthenticationCrawlerResult {

    public YahooAuthenticationCrawlerResult(boolean success, String htmlPath) {
        super(success, htmlPath);
    }

    public YahooAuthenticationCrawlerResult(boolean success, CodeType codeType, String htmlPath) {
        super(success, null, codeType, null, false, htmlPath);
    }

    public YahooAuthenticationCrawlerResult(boolean success, String reason, CodeType codeType, String img, boolean needContinue) {
        super(success, reason, codeType, img, needContinue);
    }
}
