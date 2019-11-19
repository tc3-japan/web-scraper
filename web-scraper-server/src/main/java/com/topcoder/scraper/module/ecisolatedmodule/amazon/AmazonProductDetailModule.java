package com.topcoder.scraper.module.ecisolatedmodule.amazon;

import com.topcoder.scraper.module.ecisolatedmodule.AbstractProductDetailModule;
import com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler.AmazonProductDetailCrawler;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Amazon implementation of ProductDetailModule
 */
@Component
public class AmazonProductDetailModule extends AbstractProductDetailModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonProductDetailModule.class);

  @Autowired
  public AmazonProductDetailModule(
          ProductService             productService,
          WebpageService             webpageService,
          AmazonProductDetailCrawler crawler) {
    super(productService, webpageService, crawler);
  }

  @Override
  public String getModuleType() {
    return "amazon";
  }
}
