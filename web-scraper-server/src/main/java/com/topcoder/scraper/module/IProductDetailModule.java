package com.topcoder.scraper.module;

import java.io.IOException;
import java.util.List;

/**
 * abstract product detail module
 */
public interface IProductDetailModule extends IBasicModule {
  /**
   * fetch product detail
   */
  void fetchProductDetailList(List<String> sites) throws IOException;

}
