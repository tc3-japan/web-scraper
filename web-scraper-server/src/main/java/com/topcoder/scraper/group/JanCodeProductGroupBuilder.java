package com.topcoder.scraper.group;

import java.util.LinkedList;
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
public class JanCodeProductGroupBuilder extends AbstractProductGroupBuilder {

  private static final Logger logger = LoggerFactory.getLogger(JanCodeProductGroupBuilder.class);

  @Autowired
  ProductRepository productRepository;

  @Override
  String getGroupingMethod() {
    return ProductGroupDAO.GroupingMethod.jan_code;
  }

  @Override
  protected String getSearchParameter(ProductDAO product) {
    if (product == null) {
      throw new IllegalArgumentException("product must be specified.");
    }
    return product.getJanCode();
  }

  @Override
  public List<ProductDAO> findSameProducts(ProductDAO prod) {
    logger.debug("Find products by JAN code: " + prod.getJanCode());
    List<ProductDAO> productList = new LinkedList<>();
    if (prod == null || StringUtils.isBlank(prod.getJanCode())) {
      return productList;
    }
    return this.productRepository.findByJanCode(prod.getJanCode());
  }
}
