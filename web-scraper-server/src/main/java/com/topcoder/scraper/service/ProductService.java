package com.topcoder.scraper.service;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import java.util.List;

/**
 * Interface for product and category
 */
public interface ProductService {
  void addCategoryRanking(int productId, String categoryPath, int ranking);

  void updateProduct(int productId, ProductInfo product);
  
  void updateFetchInfoStatus(int productId, String fetchInfoStatus);

  List<ProductDAO> getAllFetchInfoStatusIsNull(String ecSite);
  
  void saveProduct(String site, ProductInfo productInfo);
}
