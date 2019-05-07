package com.topcoder.scraper.module.kojima.crawler;

import java.util.List;

import com.topcoder.common.model.PurchaseHistory;

/**
 * Result for AmazonPurchaseHistoryListCrawler
 */
public class KojimaPurchaseHistoryListCrawlerResult {
  private List<PurchaseHistory> purchaseHistoryList;
  private List<String> htmlPathList;

  public KojimaPurchaseHistoryListCrawlerResult(List<PurchaseHistory> purchaseHistoryList, List<String> htmlPathList) {
    this.purchaseHistoryList = purchaseHistoryList;
    this.htmlPathList = htmlPathList;
  }

  public List<PurchaseHistory> getPurchaseHistoryList() {
    return purchaseHistoryList;
  }

  public List<String> getHtmlPathList() {
    return htmlPathList;
  }
}