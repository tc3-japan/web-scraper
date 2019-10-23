package com.topcoder.scraper.module;

import com.topcoder.scraper.module.IBasicModule;

import java.io.IOException;
import java.util.List;

/**
 * abstract purchase history list module
 */
public interface IPurchaseHistoryListModule extends IBasicModule {
  /**
   * fetch purchase history list
   */
  void fetchPurchaseHistoryList(List<String> sites) throws IOException;
}
