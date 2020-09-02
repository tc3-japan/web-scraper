package com.topcoder.scraper.module.ecisolatedmodule.kojima.crawler;

import com.topcoder.common.model.CodeType;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractAuthenticationCrawlerResult;

/**
 * Result from KojimaAuthenticationCrawler
 */
public class KojimaAuthenticationCrawlerResult extends AbstractAuthenticationCrawlerResult {

    public KojimaAuthenticationCrawlerResult(boolean success, String htmlPath) {
        super(success, htmlPath);
    }

    public KojimaAuthenticationCrawlerResult(boolean success, String reason, CodeType codeType, String img, boolean needContinue) {
        super(success, reason, codeType, img, needContinue);
    }
}
