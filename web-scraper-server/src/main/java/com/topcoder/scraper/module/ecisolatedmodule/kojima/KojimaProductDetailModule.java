package com.topcoder.scraper.module.ecisolatedmodule.kojima;

import com.topcoder.scraper.module.ecisolatedmodule.AbstractProductDetailModule;
import com.topcoder.scraper.module.ecisolatedmodule.kojima.crawler.KojimaProductDetailCrawler;
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
public class KojimaProductDetailModule extends AbstractProductDetailModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaProductDetailModule.class);

  @Autowired
  public KojimaProductDetailModule(
          ProductService             productService,
          WebpageService             webpageService,
          KojimaProductDetailCrawler crawler) {
    super(productService, webpageService, crawler);
  }

  @Override
  public String getModuleType() {
    return "kojima";
  }
}
