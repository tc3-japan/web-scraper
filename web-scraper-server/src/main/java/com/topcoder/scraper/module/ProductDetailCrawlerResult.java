package com.topcoder.scraper.module;

import com.topcoder.common.model.ProductInfo;

/**
 * Result for *ProductDetailCrawler
 */
public class ProductDetailCrawlerResult {
  private ProductInfo productInfo;
  private String htmlPath;

  public ProductDetailCrawlerResult(ProductInfo productInfo, String htmlPath) {
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