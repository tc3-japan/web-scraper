package com.topcoder.scraper.module.ecisolatedmodule.kojima.crawler;

import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractProductCrawler;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Kojima implementation of ProductDetailCrawler
 */
@Component
public class KojimaProductCrawler extends AbstractProductCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaProductCrawler.class);

  @Autowired
  public KojimaProductCrawler(WebpageService webpageService) {
    super("kojima", webpageService);
  }

  @Override
  public String getScriptSupportClassName() {
    return KojimaProductCrawlerScriptSupport.class.getName();
  }
}
