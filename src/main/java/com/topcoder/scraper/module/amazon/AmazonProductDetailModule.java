package com.topcoder.scraper.module.amazon;

import com.gargoylesoftware.htmlunit.WebClient;
import com.topcoder.scraper.config.AmazonProperty;
import com.topcoder.scraper.dao.ProductDAO;
import com.topcoder.scraper.module.ProductDetailModule;
import com.topcoder.scraper.repository.ProductRepository;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Amazon implementation of ProductDetailModule
 */
@Component
public class AmazonProductDetailModule extends ProductDetailModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonProductDetailModule.class);

  private final AmazonProperty property;
  private final WebClient webClient;
  private final ProductRepository productRepository;

  @Autowired
  public AmazonProductDetailModule(
    AmazonProperty property,
    WebClient webClient,
    ProductRepository productRepository) {
    this.property = property;
    this.webClient = webClient;
    this.productRepository = productRepository;
  }

  @Override
  public String getECName() {
    return "amazon";
  }


  @Override
  public void fetchProductDetailList() throws IOException {
    List<ProductDAO> products = productRepository.findByFetchInfoStatusIsNull();
    products.stream().forEach(product -> {
      String productUrl = property.getProductUrl() + product.getProductCode();
      LOGGER.info("Product url " + productUrl);
      // TODO what to do with url
    });
  }
}
