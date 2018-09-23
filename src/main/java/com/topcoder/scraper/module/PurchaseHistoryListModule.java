package com.topcoder.scraper.module;

import java.io.IOException;

/**
 * abstract purchase history list module
 */
public abstract class PurchaseHistoryListModule implements IBasicModule {
  /**
   * fetch purchase history list
   */
  public abstract void fetchPurchaseHistoryList() throws IOException;
}
