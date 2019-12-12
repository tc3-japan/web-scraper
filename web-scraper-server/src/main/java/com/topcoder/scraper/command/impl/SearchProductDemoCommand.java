package com.topcoder.scraper.command.impl;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.scraper.command.AbstractCommand;
import com.topcoder.scraper.exception.FetchProductDetailException;
import com.topcoder.scraper.module.IProductModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Product detail command
 */
@Component
public class SearchProductDemoCommand extends AbstractCommand<IProductModule> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchProductDemoCommand.class);

  @Autowired
  public SearchProductDemoCommand(List<IProductModule> modules) {
    super(modules);
  }

  /**
   * run fetch product detail from specific module
   * @param module module to be run
   */
  @Override
  protected void process(IProductModule module) {
    try {
      ProductDAO product;

      LOGGER.info(">>> SearchProductDemo, Amazon: ");
      product = module.searchProductInfo("amazon", "ES-W111-SR");
      LOGGER.info("ProductCode: " + product.getProductCode());
      LOGGER.info("ProductName: " + product.getProductName());
      LOGGER.info("UnitPrice: "   + product.getUnitPrice());
      LOGGER.info("ModelNo: "     + product.getModelNo());

      LOGGER.info(">>> SearchProductDemo, Kojima: ");
      product = module.searchProductInfo("kojima", "洗濯機");
      LOGGER.info("ProductCode: " + product.getProductCode());
      LOGGER.info("ProductName: " + product.getProductName());
      LOGGER.info("UnitPrice: "   + product.getUnitPrice());
      LOGGER.info("ModelNo: "     + product.getModelNo());

      LOGGER.info(">>> SearchProductDemo, Yahoo: ");
      product = module.searchProductInfo("yahoo", "毛皮");
      LOGGER.info("ProductCode: " + product.getProductCode());
      LOGGER.info("ProductName: " + product.getProductName());
      LOGGER.info("UnitPrice: "   + product.getUnitPrice());
      LOGGER.info("ModelNo: "     + product.getModelNo());

    } catch (IOException e) {
      LOGGER.error("Fail to fetch product detail list", e);
      throw new FetchProductDetailException();
    }
    LOGGER.info("Successfully product detail list");
  }

}
