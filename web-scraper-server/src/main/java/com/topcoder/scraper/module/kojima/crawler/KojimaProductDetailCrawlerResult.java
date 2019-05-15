package com.topcoder.scraper.module.kojima.crawler;

import com.topcoder.common.model.ProductInfo;
import com.topcoder.scraper.module.amazon.crawler.AmazonProductDetailCrawlerResult;

public class KojimaProductDetailCrawlerResult extends AmazonProductDetailCrawlerResult {

  public KojimaProductDetailCrawlerResult(ProductInfo productInfo, String htmlPath) {
    super(productInfo, htmlPath);
  }
}
