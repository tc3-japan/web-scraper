package com.topcoder.scraper.module;

import java.io.IOException;

/**
 * abstract product detail module
 */
public abstract class ProductDetailModule implements IBasicModule {
  /**
   * fetch product detail
   */
  public abstract void fetchProductDetailList() throws IOException;
}
