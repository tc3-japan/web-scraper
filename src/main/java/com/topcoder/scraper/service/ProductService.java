package com.topcoder.scraper.service;

import com.topcoder.scraper.dao.ProductDAO;
import java.util.List;

/**
 * Interface for product and category
 */
public interface ProductService {
  void addCategoryRanking(int productId, String categoryPath, int ranking);

  void update(int productId, String price, String fetchInfoStatus);

  List<ProductDAO> getAllFetchInfoStatusIsNull();
}
