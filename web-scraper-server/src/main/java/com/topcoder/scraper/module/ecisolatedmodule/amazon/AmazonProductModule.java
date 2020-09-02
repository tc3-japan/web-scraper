package com.topcoder.scraper.module.ecisolatedmodule.amazon;

import com.topcoder.scraper.module.ecisolatedmodule.AbstractProductModule;
import com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler.AmazonProductCrawler;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Amazon implementation of ProductModule
 */
@Component
public class AmazonProductModule extends AbstractProductModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonProductModule.class);

  @Autowired
  public AmazonProductModule(
          ProductService             productService,
          WebpageService             webpageService,
          AmazonProductCrawler       crawler) {
    super(productService, webpageService, crawler);
  }

  @Override
  public String getModuleType() {
    return "amazon";
  }
}
