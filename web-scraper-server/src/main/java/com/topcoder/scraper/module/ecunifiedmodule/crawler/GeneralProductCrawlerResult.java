package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import com.topcoder.common.model.ProductInfo;

public class GeneralProductCrawlerResult {
  private ProductInfo productInfo;
  private String htmlPath;
  private String productCode;

  public GeneralProductCrawlerResult(ProductInfo productInfo, String htmlPath) {
    this.productInfo = productInfo;
    this.htmlPath    = htmlPath;
  }

  public GeneralProductCrawlerResult(String productCode, String htmlPath) {
    this.productCode = productCode;
    this.htmlPath    = htmlPath;
  }

  public ProductInfo getProductInfo() {
    return productInfo;
  }

  public String getHtmlPath() {
    return htmlPath;
  }

  public String getProductCode() {
    return productCode;
  }

}
