package com.topcoder.scraper.command.impl;

import com.topcoder.scraper.command.AbstractCommand;
import com.topcoder.scraper.exception.FetchProductDetailException;
import com.topcoder.scraper.module.ProductDetailModule;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Product detail command
 */
@Component
public class ProductDetailCommand extends AbstractCommand<ProductDetailModule> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductDetailCommand.class);

  @Autowired
  public ProductDetailCommand(List<ProductDetailModule> modules) {
    super(modules);
  }
  
  /**
   * run fetch product detail from specific module
   * @param module module to be run
   */
  @Override
  protected void process(ProductDetailModule module) {
    try {
      module.fetchProductDetailList();
    } catch (IOException e) {
      LOGGER.error("Fail to fetch product detail list", e);
      throw new FetchProductDetailException();
    }
    LOGGER.info("Successfully product detail list");
  }

}
