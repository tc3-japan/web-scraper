package com.topcoder.scraper.module.yahoo.crawler;

import java.util.List;

import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.scraper.module.amazon.crawler.AmazonPurchaseHistoryListCrawlerResult;

public class YahooPurchaseHistoryListCrawlerResult extends AmazonPurchaseHistoryListCrawlerResult {

  public YahooPurchaseHistoryListCrawlerResult(List<PurchaseHistory> purchaseHistoryList, List<String> htmlPathList) {
    super(purchaseHistoryList, htmlPathList);
  }
}