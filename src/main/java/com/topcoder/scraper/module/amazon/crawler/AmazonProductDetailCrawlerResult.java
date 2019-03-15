package com.topcoder.scraper.module.amazon.crawler;

import com.topcoder.scraper.model.ProductInfo;

/**
 * Result from AmazonProductDetailCrawler
 */
public class AmazonProductDetailCrawlerResult {
  private ProductInfo productInfo;
  private String htmlPath;

  public AmazonProductDetailCrawlerResult(ProductInfo productInfo, String htmlPath) {
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
