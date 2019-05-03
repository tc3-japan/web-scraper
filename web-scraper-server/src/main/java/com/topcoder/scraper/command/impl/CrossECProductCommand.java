package com.topcoder.scraper.command.impl;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.dao.ProductGroupDAO;
import com.topcoder.common.repository.ProductGroupRepository;
import com.topcoder.common.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This will group product information of all products where group_status is null or uninitialized,
 * to the appropriate product_group table.
 */
@Component
@Transactional
public class CrossECProductCommand {

  @Autowired
  ProductRepository productRepository;

  @Autowired
  ProductGroupRepository productGroupRepository;

  private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  public void run(ApplicationArguments arguments) {
    List<ProductDAO> productDAOList = productRepository.findByGroupStatusIsNullOrProductGroupIdIsNull();
    logger.info("found " + productDAOList.size() + " un grouped product");
    Map<String, List<ProductDAO>> productDAOMap = new HashMap<>();
    productDAOList.forEach(productDAO -> {
      if (productDAO.getModelNo() != null) {
        List<ProductDAO> productDAOS = productDAOMap.get(productDAO.getModelNo());
        if (productDAOS == null) {
          productDAOS = new LinkedList<>();
        }
        productDAOS.add(productDAO);
        productDAOMap.put(productDAO.getModelNo(), productDAOS);
      }
    });

    productDAOMap.forEach((key, productDAOS)->{
      logger.info("start group " + key + " with item count = " + productDAOS.size());
      if (productDAOS.size() <= 1) {
        logger.info("skip group " + key + " , because of item count < 2");
        return;
      }
      ProductGroupDAO groupDAO = productGroupRepository.getByModelNo(key);
      if (groupDAO == null) {
        groupDAO = new ProductGroupDAO();
        groupDAO.setModelNo(key);
        groupDAO.setConfirmationStatus(ProductGroupDAO.ConfirmationStatus.unconfirmed);
        groupDAO.setGroupingMethod(ProductGroupDAO.GroupingMethod.same_no);
      }
      groupDAO.setUpdateAt(Date.from(Instant.now()));
      productGroupRepository.save(groupDAO);

      ProductGroupDAO finalGroupDAO = groupDAO;
      productDAOS.forEach(product -> {
        product.setGroupStatus(ProductDAO.GroupStatus.grouped);
        product.setProductGroupId(finalGroupDAO.getId());
        productRepository.save(product);
      });
    });
  }
}
