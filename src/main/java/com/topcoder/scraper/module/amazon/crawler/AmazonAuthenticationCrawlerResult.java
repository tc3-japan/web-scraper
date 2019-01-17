package com.topcoder.scraper.module.amazon.crawler;

/**
 * Result from AmazonAuthenticationCrawler
 */
public class AmazonAuthenticationCrawlerResult {

  private boolean success;
  private String htmlPath;

  public AmazonAuthenticationCrawlerResult(boolean success, String htmlPath) {
    this.success = success;
    this.htmlPath = htmlPath;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getHtmlPath() {
    return htmlPath;
  }
}
