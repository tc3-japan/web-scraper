package com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler;

import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractProductCrawler;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Yahoo implementation of ProductDetailCrawler
 */
@Component
public class YahooProductCrawler extends AbstractProductCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(YahooProductCrawler.class);

    @Autowired
    public YahooProductCrawler(WebpageService webpageService) {
        super("yahoo", webpageService);
    }

    @Override
    public String getScriptSupportClassName() {
        return YahooProductCrawlerScriptSupport.class.getName();
    }
}
