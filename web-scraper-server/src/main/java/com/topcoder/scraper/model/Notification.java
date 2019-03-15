package com.topcoder.scraper.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Notification {

  private String ecSite;

  private String page;

  private String pageKey;

  private Date detectionTime;

  private List<String> htmlPaths = new LinkedList<>();

  private String logPath;

  public Notification() {
  }

  public Notification(String ecSite, String page, String pageKey) {
    this.ecSite = ecSite;
    this.page = page;
    this.pageKey = pageKey;
  }

  public String getEcSite() {
    return ecSite;
  }

  public void setEcSite(String ecSite) {
    this.ecSite = ecSite;
  }

  public String getPage() {
    return page;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public String getPageKey() {
    return pageKey;
  }

  public void setPageKey(String pageKey) {
    this.pageKey = pageKey;
  }

  public Date getDetectionTime() {
    return detectionTime;
  }

  public void setDetectionTime(Date detectionTime) {
    this.detectionTime = detectionTime;
  }

  public List<String> getHtmlPaths() {
    return htmlPaths;
  }

  public void setHtmlPaths(List<String> htmlPaths) {
    this.htmlPaths = htmlPaths;
  }

  public String getLogPath() {
    return logPath;
  }

  public void setLogPath(String logPath) {
    this.logPath = logPath;
  }

  public void addHtmlPath(String path) {
    this.htmlPaths.add(path);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("ecSite", ecSite)
      .append("page", page)
      .append("pageKey", pageKey)
      .append("detectionTime", detectionTime)
      .append("htmlPaths", htmlPaths)
      .append("logPath", logPath)
      .toString();
  }
}
