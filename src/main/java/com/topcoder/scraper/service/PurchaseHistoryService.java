package com.topcoder.scraper.service;

import com.topcoder.scraper.model.PurchaseHistory;
import java.util.List;
import java.util.Optional;

/**
 * Interface for purchase history
 */
public interface PurchaseHistoryService {
  /**
   * incremental save purchase history list
   * @param site site name
   * @param list purchase history list
   */
  void save(String site, List<PurchaseHistory> list);
  List<PurchaseHistory> listAll(String site);

  Optional<PurchaseHistory> fetchLast(String site);
}
