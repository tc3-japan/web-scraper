package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import com.topcoder.common.model.ProductInfo;

public class AbstractProductCrawlerResult {
    private ProductInfo productInfo;
    private String htmlPath;

    public AbstractProductCrawlerResult(ProductInfo productInfo, String htmlPath) {
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
