package com.topcoder.scraper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * amazon related property
 */
@Configuration
@ConfigurationProperties(prefix = "amazon")
public class AmazonProperty {

  private String username;
  private String password;
  private String url;
  private String historyUrl;

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getUrl() {
    return url;
  }

  public String getHistoryUrl() {
    return historyUrl;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setHistoryUrl(String historyUrl) {
    this.historyUrl = historyUrl;
  }
}
