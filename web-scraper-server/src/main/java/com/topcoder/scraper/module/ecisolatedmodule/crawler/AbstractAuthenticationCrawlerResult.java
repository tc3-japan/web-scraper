package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import com.topcoder.common.model.CodeType;
import lombok.Data;

/**
 * Result from AbstractAuthenticationCrawler
 */
@Data
public class AbstractAuthenticationCrawlerResult {

  private boolean success;
  private String reason;
  private CodeType codeType;
  private String img;
  private String htmlPath;
  private boolean needContinue;

  public AbstractAuthenticationCrawlerResult(boolean success, String htmlPath) {
    this.success = success;
    this.htmlPath = htmlPath;
  }

  public AbstractAuthenticationCrawlerResult(boolean success, String reason, CodeType codeType, String img, boolean needContinue) {
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
