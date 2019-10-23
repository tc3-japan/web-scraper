package com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler;

import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractProductDetailCrawler;
import com.topcoder.scraper.service.WebpageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Amazon implementation of ProductDetailCrawler
 */
@Component
public class AmazonProductDetailCrawler extends AbstractProductDetailCrawler {

  @Autowired
  public AmazonProductDetailCrawler(WebpageService webpageService) {
    super("amazon", webpageService);
  }

  @Override
  public String getScriptSupportClassName() {
    return AmazonProductDetailCrawlerScriptSupport.class.getName();
  }
}
