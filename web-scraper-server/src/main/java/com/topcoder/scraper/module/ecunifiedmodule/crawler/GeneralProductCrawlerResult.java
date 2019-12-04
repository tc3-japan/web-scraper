package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import com.topcoder.common.model.ProductInfo;

public class GeneralProductCrawlerResult {
  private ProductInfo productInfo;
  private String htmlPath;

  public GeneralProductCrawlerResult(ProductInfo productInfo, String htmlPath) {
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
