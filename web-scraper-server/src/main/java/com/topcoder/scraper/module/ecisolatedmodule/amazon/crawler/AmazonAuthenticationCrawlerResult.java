package com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler;

import com.topcoder.common.model.CodeType;
import lombok.Data;

/**
 * Result from AmazonAuthenticationCrawler
 */
@Data
public class AmazonAuthenticationCrawlerResult {

  private boolean success;
  private String reason;
  private CodeType codeType;
  private String img;
  private String htmlPath;
  private boolean needContinue;

  public AmazonAuthenticationCrawlerResult(boolean success, String htmlPath) {
    this.success = success;
    this.htmlPath = htmlPath;
  }

  public AmazonAuthenticationCrawlerResult(boolean success, String reason, CodeType codeType, String img, boolean needContinue) {
    this.success = success;
    this.reason = reason;
    this.codeType = codeType;
    this.img = img;
    this.needContinue = needContinue;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getHtmlPath() {
    return htmlPath;
  }
}
