package com.topcoder.scraper.module.amazon;

import com.gargoylesoftware.htmlunit.WebClient;
import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ProductDetailModule;
import com.topcoder.scraper.module.amazon.crawler.AmazonProductDetailCrawler;
import com.topcoder.scraper.module.amazon.crawler.AmazonProductDetailCrawlerResult;
import com.topcoder.scraper.module.kojima.crawler.KojimaProductDetailCrawler;
import com.topcoder.scraper.module.kojima.crawler.KojimaProductDetailCrawlerResult;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
  private final TrafficWebClient webClient;
  private final ProductService productService;
  private final WebpageService webpageService;

  @Autowired
  public AmazonProductDetailModule(
    AmazonProperty property,
    ProductService productService,
    WebpageService webpageService) {
    this.property = property;
    this.webClient = new TrafficWebClient(0, false);
    this.productService = productService;
    this.webpageService = webpageService;
  }

  @Override
  public String getECName() {
    return "amazon";
  }


  @Override
  public void fetchProductDetailList() {
    List<ProductDAO> products = this.productService.getAllFetchInfoStatusIsNull(getECName());
    AmazonProductDetailCrawler crawler = new AmazonProductDetailCrawler(getECName(), property, webpageService);

    products.forEach(product -> {
      try {
        fetchProductDetail(crawler, product.getId(), product.getProductCode());
      } catch (IOException | IllegalStateException e) {
        LOGGER.error(String.format("Fail to fetch product %s, please try again.", product.getProductCode()));
      }
    });
  }

  /**
   * Fetch product information from amazon
   * and save in database
   * @param crawler the crawler
   * @param productId the product id
   * @param productCode the product code
   * @throws IOException webclient exception
   */
  private void fetchProductDetail(AmazonProductDetailCrawler crawler, int productId, String productCode) throws IOException {
    AmazonProductDetailCrawlerResult crawlerResult = crawler.fetchProductInfo(webClient, productCode, false);
    ProductInfo productInfo = crawlerResult.getProductInfo();

    // save updated information
    productService.updateProduct(productId, productInfo);
    for (int i = 0; i < productInfo.getCategoryList().size(); i++) {
      String category = productInfo.getCategoryList().get(i);
      Integer rank = productInfo.getRankingList().get(i);
      productService.addCategoryRanking(productId, category, rank);
    }

    productService.updateFetchInfoStatus(productId, "updated");
  }

  @Override
  public ProductDAO crossEcProduct(String modelNo) throws IOException {
	  
	  AmazonProductDetailCrawler crawler = new AmazonProductDetailCrawler(getECName(), property, webpageService);
	  AmazonProductDetailCrawlerResult crawlerResult = crawler.serarchProductAndFetchProductInfoByModelNo(webClient, modelNo, false);
	  ProductInfo productInfo = Objects.isNull(crawlerResult) ? null : crawlerResult.getProductInfo();
	    
	  if (Objects.isNull(productInfo)) {
	    LOGGER.warn("Unable to obtain a cross ec product information about: " + modelNo);
	    return null;
	  }
	  
	  return new ProductDAO(getECName(), productInfo);
  }
}
