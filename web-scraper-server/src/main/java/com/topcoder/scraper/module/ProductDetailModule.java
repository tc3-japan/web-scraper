package com.topcoder.scraper.module;

import java.io.IOException;

import com.topcoder.common.dao.ProductDAO;

/**
 * abstract product detail module
 */
public abstract class ProductDetailModule implements IBasicModule {
  /**
   * fetch product detail
   */
  public abstract void fetchProductDetailList() throws IOException;
  
  /**
   * cross ec product
   */
  public abstract ProductDAO crossEcProduct(String modelNo) throws IOException;
}
