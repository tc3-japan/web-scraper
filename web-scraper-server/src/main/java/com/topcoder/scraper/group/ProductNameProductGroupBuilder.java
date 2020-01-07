package com.topcoder.scraper.group;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.dao.ProductGroupDAO;
import com.topcoder.common.service.SolrService;
import com.topcoder.common.service.SolrService.SolrPorduct;

@Component
public class ProductNameProductGroupBuilder extends AbstractProductGroupBuilder {

  private static final Logger logger = LoggerFactory.getLogger(ProductNameProductGroupBuilder.class);

  @Autowired
  private SolrService solrService;

  @Value("${scraper.matching.mlt_score_threthold:1.0}")
  Float scoreThreshold;

  @Override
  String getGroupingMethod() {
    return ProductGroupDAO.GroupingMethod.product_name;
  }

  @Override
  List<ProductDAO> findSameProducts(ProductDAO prod) {
    List<ProductDAO> result = new LinkedList<>();
    result.add(prod);
    try {
      List<SolrPorduct> similarProducts = this.solrService.searchSimilarProducts(prod);
      logger.info(String.format("found %d products from Search Index", similarProducts.size()));
      similarProducts.forEach(p -> {
        if (p.getScore() != null && p.getScore() < this.scoreThreshold) {
          logger.info(String.format("#%d [%s] is skipped because of its lower score: %.5f < %.3f", p.getScore(),
              this.scoreThreshold));
          return;
        }
        if (!compareProducts(prod, p)) {
          logger.info(String.format("#%d [%s] is skipped."));
          return;
        }
        result.add(p.toProductDAO());
      });
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    return result;
  }

  @Override
  String getSearchParameter(ProductDAO product) {
    return product.getProductName();
  }
}
