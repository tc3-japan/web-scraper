package com.topcoder.scraper.module;

import com.topcoder.scraper.module.IBasicModule;

import java.io.IOException;
import java.util.List;

/**
 * abstract purchase history list module
 */
public abstract class IPurchaseHistoryListModule implements IBasicModule {
  /**
   * fetch purchase history list
   */
  public abstract void fetchPurchaseHistoryList(List<String> sites) throws IOException;
}
