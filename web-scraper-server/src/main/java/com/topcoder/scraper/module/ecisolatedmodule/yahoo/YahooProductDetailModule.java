package com.topcoder.scraper.module.ecisolatedmodule.yahoo;

import com.topcoder.scraper.module.ecisolatedmodule.AbstractProductModule;
import com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler.YahooProductCrawler;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Yahoo implementation of ProductDetailModule
 */
@Component
public class YahooProductDetailModule extends AbstractProductModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(YahooProductDetailModule.class);

    @Autowired
    public YahooProductDetailModule(
            ProductService productService,
            WebpageService webpageService,
            YahooProductCrawler crawler) {
        super(productService, webpageService, crawler);
    }

    @Override
    public String getModuleType() {
        return "yahoo";
    }
}
