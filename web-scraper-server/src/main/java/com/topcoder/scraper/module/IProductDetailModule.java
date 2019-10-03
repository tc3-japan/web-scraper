package com.topcoder.scraper.module;

import com.topcoder.common.dao.ProductDAO;

import java.io.IOException;
import java.util.List;

/**
 * abstract product detail module
 */
public abstract class IProductDetailModule implements IBasicModule {
  /**
   * fetch product detail
   */
  public abstract void fetchProductDetailList(List<String> sites) throws IOException;

  /**
   * cross ec product
   */
  public abstract ProductDAO crossEcProduct(String modelNo) throws IOException;
}
