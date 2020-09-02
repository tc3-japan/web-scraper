package com.topcoder.scraper.module.ecunifiedmodule.crawler;

public class GeneralProductSearchCrawlerResult {
    private String htmlPath;
    private String productCode;

    public GeneralProductSearchCrawlerResult(String productCode, String htmlPath) {
        this.productCode = productCode;
        this.htmlPath = htmlPath;
    }

    public String getHtmlPath() {
        return htmlPath;
    }

    public String getProductCode() {
        return productCode;
    }

}
