package com.topcoder.scraper.module.ecisolatedmodule.rakuten.crawler;

import com.topcoder.common.model.CodeType;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractAuthenticationCrawlerResult;

/**
 * Result from RakutenAuthenticationCrawler
 */
public class RakutenAuthenticationCrawlerResult extends AbstractAuthenticationCrawlerResult {

    public RakutenAuthenticationCrawlerResult(boolean success, String htmlPath) {
        super(success, htmlPath);
    }

    public RakutenAuthenticationCrawlerResult(boolean success, String reason, CodeType codeType, String img, boolean needContinue) {
        super(success, reason, codeType, img, needContinue);
    }
}
