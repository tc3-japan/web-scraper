package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import com.topcoder.common.model.ProductInfo;

public class GeneralProductDetailCrawlerResult {
  private ProductInfo productInfo;
  private String htmlPath;

  public GeneralProductDetailCrawlerResult(ProductInfo productInfo, String htmlPath) {
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
