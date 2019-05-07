package com.topcoder.scraper.module.kojima.crawler;

import com.topcoder.common.model.ProductInfo;

public class KojimaProductDetailCrawlerResult {
  private ProductInfo productInfo;
  private String htmlPath;

  public KojimaProductDetailCrawlerResult(ProductInfo productInfo, String htmlPath) {
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
