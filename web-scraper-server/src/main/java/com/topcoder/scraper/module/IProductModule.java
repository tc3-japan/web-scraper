package com.topcoder.scraper.module;

import com.topcoder.common.dao.ProductDAO;

import java.io.IOException;
import java.util.List;

/**
 * abstract product detail module
 */
public interface IProductModule extends IBasicModule {
  /**
   * fetch product detail
   */
  void fetchProductDetailList(List<String> sites) throws IOException;

  /**
   * search product for cross ec product
   */
  ProductDAO searchProductInfo(String siteName, String searchKey) throws IOException;
}
