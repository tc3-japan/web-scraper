package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import com.topcoder.common.model.PurchaseHistory;

import java.util.List;

/**
 * Result for AmazonPurchaseHistoryCrawler
 */
public class AbstractPurchaseHistoryCrawlerResult {
  private List<PurchaseHistory> purchaseHistoryList;
  private List<String> htmlPathList;

  public AbstractPurchaseHistoryCrawlerResult(List<PurchaseHistory> purchaseHistoryList, List<String> htmlPathList) {
    this.purchaseHistoryList = purchaseHistoryList;
    this.htmlPathList        = htmlPathList;
  }

  public List<PurchaseHistory> getPurchaseHistoryList() {
    return purchaseHistoryList;
  }

  public List<String> getHtmlPathList() {
    return htmlPathList;
  }
}
