package com.topcoder.scraper.module;

import com.topcoder.common.model.PurchaseHistory;
import java.util.List;

/**
 * Result for AmazonPurchaseHistoryListCrawler
 */
public class PurchaseHistoryListCrawlerResult {
  private List<PurchaseHistory> purchaseHistoryList;
  private List<String> htmlPathList;

  public PurchaseHistoryListCrawlerResult(List<PurchaseHistory> purchaseHistoryList, List<String> htmlPathList) {
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
