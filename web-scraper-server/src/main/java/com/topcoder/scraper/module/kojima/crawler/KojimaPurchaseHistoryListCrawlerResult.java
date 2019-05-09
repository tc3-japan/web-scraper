package com.topcoder.scraper.module.kojima.crawler;

import java.util.List;

import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.scraper.module.amazon.crawler.AmazonPurchaseHistoryListCrawlerResult;

public class KojimaPurchaseHistoryListCrawlerResult extends AmazonPurchaseHistoryListCrawlerResult {

  public KojimaPurchaseHistoryListCrawlerResult(List<PurchaseHistory> purchaseHistoryList, List<String> htmlPathList) {
    super(purchaseHistoryList, htmlPathList);
  }
}