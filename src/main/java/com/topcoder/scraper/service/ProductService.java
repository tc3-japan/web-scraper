package com.topcoder.scraper.service;

import com.topcoder.scraper.dao.ProductDAO;
import java.util.List;

/**
 * Interface for product and category
 */
public interface ProductService {
  void addCategoryRanking(int productId, String categoryPath, int ranking);

  void updatePrice(int productId, String price);
  void updateFetchInfoStatus(int productId, String fetchInfoStatus);

  List<ProductDAO> getAllFetchInfoStatusIsNull();
}
