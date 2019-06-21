package com.topcoder.scraper.module.yahoo.crawler;

import com.topcoder.common.model.ProductInfo;

/**
 * Result from YahooProductDetailCrawler
 */
public class YahooProductDetailCrawlerResult {
  private ProductInfo productInfo;
  private String htmlPath;

  public YahooProductDetailCrawlerResult(ProductInfo productInfo, String htmlPath) {
    this.productInfo = productInfo;
    this.htmlPath = htmlPath;
  }

  public ProductInfo getProductInfo() {
    return productInfo;
  }

  public String getHtmlPath() {
    return htmlPath;
  }
}
