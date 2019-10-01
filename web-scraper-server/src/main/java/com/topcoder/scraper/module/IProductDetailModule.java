package com.topcoder.scraper.module;

import java.io.IOException;
import java.util.List;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.scraper.module.IBasicModule;

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
