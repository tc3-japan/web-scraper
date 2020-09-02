package com.topcoder.scraper.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.dao.ProductGroupDAO;
import com.topcoder.common.repository.ProductRepository;

@Component
public class ModelNumberProductGroupBuilder extends AbstractProductGroupBuilder {

  private static final Logger logger = LoggerFactory.getLogger(ModelNumberProductGroupBuilder.class);

  @Autowired
  ProductRepository productRepository;

  @Override
  String getGroupingMethod() {
    return ProductGroupDAO.GroupingMethod.same_no;
  }

  @Override
  protected String getSearchParameter(ProductDAO product) {
    if (product == null) {
      throw new IllegalArgumentException("product must be specified.");
    }
    return product.getModelNo();
  }

  @Override
  public List<ProductDAO> findSameProducts(ProductDAO prod) {
    logger.info("Searching for products by Model no: " + prod.getModelNo());
    if (prod == null || StringUtils.isBlank(prod.getModelNo())) {
      return new ArrayList<ProductDAO>();
    }
    return this.productRepository.findByModelNo(prod.getModelNo());
  }
}
