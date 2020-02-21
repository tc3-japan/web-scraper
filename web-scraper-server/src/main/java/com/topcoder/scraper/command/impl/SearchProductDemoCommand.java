package com.topcoder.scraper.command.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.scraper.command.AbstractCommand;
import com.topcoder.scraper.exception.FetchProductDetailException;
import com.topcoder.scraper.module.IProductModule;

/**
 * Search Product demo command
 */
// TODO : re-consider whether this class is needed or not.
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
      product = module.searchProductInfo("amazon", "GZ-E109KC-R");
      if (product != null) {
        LOGGER.info("ProductCode: " + product.getProductCode());
        LOGGER.info("ProductName: " + product.getProductName());
        LOGGER.info("UnitPrice: " + product.getUnitPrice());
        LOGGER.info("ModelNo: " + product.getModelNo());
        LOGGER.info("JANCode: " + product.getJanCode());
      } else {
        LOGGER.warn(String.format("No product(%s) found in %s", "GZ-E109KC-R", "amazon"));
      }

      LOGGER.info(">>> SearchProductDemo, Kojima: ");
      product = module.searchProductInfo("kojima", "GZ-E109KC-R");
      if (product != null) {
        LOGGER.info("ProductCode: " + product.getProductCode());
        LOGGER.info("ProductName: " + product.getProductName());
        LOGGER.info("UnitPrice: " + product.getUnitPrice());
        LOGGER.info("ModelNo: " + product.getModelNo());
        LOGGER.info("JANCode: " + product.getJanCode());
      } else {
        LOGGER.warn(String.format("No product(%s) found in %s", "GZ-E109KC-R", "Kojima"));
      }

      LOGGER.info(">>> SearchProductDemo, Yahoo: ");
      product = module.searchProductInfo("yahoo", "GZ-E109KC-R");
      if (product != null) {
        LOGGER.info("ProductCode: " + product.getProductCode());
        LOGGER.info("ProductName: " + product.getProductName());
        LOGGER.info("UnitPrice: " + product.getUnitPrice());
        LOGGER.info("ModelNo: " + product.getModelNo());
        LOGGER.info("JANCode: " + product.getJanCode());
      } else {
        LOGGER.warn(String.format("No product(%s) found in %s", "GZ-E109KC-R", "yahoo"));
      }

      LOGGER.info(">>> SearchProductDemo, Rakuten: ");
      product = module.searchProductInfo("rakuten", "HandyCam");
      if (product != null) {
        LOGGER.info("ProductCode: " + product.getProductCode());
        LOGGER.info("ProductName: " + product.getProductName());
        LOGGER.info("UnitPrice: " + product.getUnitPrice());
        LOGGER.info("ModelNo: " + product.getModelNo());
        LOGGER.info("JANCode: " + product.getJanCode());
      } else {
        LOGGER.warn(String.format("No product(%s) found in %s", "GZ-E109KC-R", "rakuten"));
      }
    } catch (IOException e) {
      LOGGER.error("Fail to fetch product detail list", e);
      throw new FetchProductDetailException();
    }
    LOGGER.info("Successfully product detail list");
  }

}
