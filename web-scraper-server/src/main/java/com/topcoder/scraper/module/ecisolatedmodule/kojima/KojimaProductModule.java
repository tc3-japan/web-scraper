package com.topcoder.scraper.module.ecisolatedmodule.kojima;

import com.topcoder.scraper.module.ecisolatedmodule.AbstractProductModule;
import com.topcoder.scraper.module.ecisolatedmodule.kojima.crawler.KojimaProductCrawler;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Kojima implementation of ProductDetailModule
 */
@Component
public class KojimaProductModule extends AbstractProductModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(KojimaProductModule.class);

    @Autowired
    public KojimaProductModule(
            ProductService productService,
            WebpageService webpageService,
            KojimaProductCrawler crawler) {
        super(productService, webpageService, crawler);
    }

    @Override
    public String getModuleType() {
        return "kojima";
    }
}
