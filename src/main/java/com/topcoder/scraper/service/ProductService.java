package com.topcoder.scraper.service;

import com.topcoder.scraper.dao.ProductDAO;
import com.topcoder.scraper.model.ProductInfo;
import java.util.List;

/**
 * Interface for product and category
 */
public interface ProductService {
  void addCategoryRanking(int productId, String categoryPath, int ranking);

  void updateProduct(int productId, ProductInfo product);
  void updateFetchInfoStatus(int productId, String fetchInfoStatus);

  List<ProductDAO> getAllFetchInfoStatusIsNull();
}
