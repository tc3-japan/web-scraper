package com.topcoder.scraper.module.general;

import com.topcoder.common.model.ProductInfo;

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
