package com.topcoder.scraper.module.ecisolatedmodule.kojima.crawler;

import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractProductDetailCrawler;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Kojima implementation of ProductDetailCrawler
 */
@Component
public class KojimaProductDetailCrawler extends AbstractProductDetailCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaProductDetailCrawler.class);

  @Autowired
  public KojimaProductDetailCrawler(WebpageService webpageService) {
    super("kojima", webpageService);
  }

  @Override
  public String getScriptSupportClassName() {
    return KojimaProductDetailCrawlerScriptSupport.class.getName();
  }
}
