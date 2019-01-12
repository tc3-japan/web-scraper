package com.topcoder.scraper.module.amazon.crawler;

import com.topcoder.scraper.model.PurchaseHistory;
import java.util.List;

/**
 * Result for AmazonPurchaseHistoryListCrawler
 */
public class AmazonPurchaseHistoryListCrawlerResult {
  private List<PurchaseHistory> purchaseHistoryList;
  private List<String> htmlPathList;

  public AmazonPurchaseHistoryListCrawlerResult(List<PurchaseHistory> purchaseHistoryList, List<String> htmlPathList) {
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
