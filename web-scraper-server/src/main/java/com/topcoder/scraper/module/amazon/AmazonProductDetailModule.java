package com.topcoder.scraper.module.amazon;

import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ProductDetailModule;
import com.topcoder.scraper.module.amazon.crawler.AmazonProductDetailCrawler;

import com.topcoder.scraper.module.general.ProductDetailCrawler;
import com.topcoder.scraper.module.general.ProductDetailCrawlerResult;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Amazon implementation of ProductDetailModule
 */
@Component
public class AmazonProductDetailModule extends ProductDetailModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonProductDetailModule.class);

  private final AmazonProperty property;
  private final ProductService productService;
  private final WebpageService webpageService;

  @Autowired
  public AmazonProductDetailModule(
    AmazonProperty property,
    ProductService productService,
    WebpageService webpageService) {
    this.property = property;
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
    // fixme
    //AmazonProductDetailCrawler crawler = new AmazonProductDetailCrawler(getECName(), property, webpageService);
    ProductDetailCrawler crawler = new ProductDetailCrawler(getECName(), webpageService);

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
  // fixme
  //private void fetchProductDetail(AmazonProductDetailCrawler crawler, int productId, String productCode) throws IOException {
  private void fetchProductDetail(ProductDetailCrawler crawler, int productId, String productCode) throws IOException {
    TrafficWebClient webClient = new TrafficWebClient(0, false);

    // fixme
    //AmazonProductDetailCrawlerResult crawlerResult = crawler.fetchProductInfo(webClient, productCode, true);
    ProductDetailCrawlerResult crawlerResult = crawler.fetchProductInfo(webClient, productCode);
    webClient.finishTraffic();

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
    TrafficWebClient webClient = new TrafficWebClient(0, false);

	  AmazonProductDetailCrawler crawler = new AmazonProductDetailCrawler(getECName(), property, webpageService);
    ProductDetailCrawlerResult crawlerResult = crawler.serarchProductAndFetchProductInfoByModelNo(webClient, modelNo, true);
    webClient.finishTraffic();

    ProductInfo productInfo = Objects.isNull(crawlerResult) ? null : crawlerResult.getProductInfo();
	    
	  if (Objects.isNull(productInfo) || productInfo.getModelNo() == null) {
	    LOGGER.warn("Unable to obtain a cross ec product information about: " + modelNo);
	    return null;
	  }

	  return new ProductDAO(getECName(), productInfo);
  }
}
