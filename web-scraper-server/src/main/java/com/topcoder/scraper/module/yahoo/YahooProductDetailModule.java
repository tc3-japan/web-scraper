package com.topcoder.scraper.module.yahoo;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ProductDetailModule;
import com.topcoder.scraper.module.yahoo.crawler.YahooProductDetailCrawler;
import com.topcoder.scraper.module.yahoo.crawler.YahooProductDetailCrawlerResult;
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
 * Yahoo implementation of ProductDetailModule
 */
@Component
public class YahooProductDetailModule extends ProductDetailModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooProductDetailModule.class);

  //private final YahooProperty property;
  private final ProductService productService;
  private final WebpageService webpageService;

  @Autowired
  public YahooProductDetailModule(
          //YahooProperty property,
          ProductService productService,
          WebpageService webpageService) {
    //this.property = property;
    this.productService = productService;
    this.webpageService = webpageService;
  }

  @Override
  public String getECName() {
    return "yahoo";
  }

  @Override
  public void fetchProductDetailList() {

    List<ProductDAO> products = this.productService.getAllFetchInfoStatusIsNull(getECName());
    YahooProductDetailCrawler crawler = new YahooProductDetailCrawler(getECName(), webpageService);

    products.forEach(product -> {
      try {
        fetchProductDetail(crawler, product.getId(), product.getProductCode());
      } catch (IOException | IllegalStateException e) {
        LOGGER.error(String.format("Fail to fetch product %s, please try again.", product.getProductCode()));
      }
    });
  }

  /**
   * Fetch product information from yahoo
   * and save in database
   * @param crawler the crawler
   * @param productId the product id
   * @param productCode the product code
   * @throws IOException webclient exception
   */
  private void fetchProductDetail(YahooProductDetailCrawler crawler, int productId, String productCode) throws IOException {

    TrafficWebClient webClient = new TrafficWebClient(0, false);
    YahooProductDetailCrawlerResult crawlerResult = crawler.fetchProductInfo(webClient, productCode, true);
    webClient.finishTraffic();
    ProductInfo productInfo = crawlerResult.getProductInfo();

    if(productInfo != null) {
      // save updated information
      productService.updateProduct(productId, productInfo);
      for (int i = 0; i < productInfo.getCategoryList().size(); i++) {
        String category = productInfo.getCategoryList().get(i);
        Integer rank = productInfo.getRankingList().get(i);
        productService.addCategoryRanking(productId, category, rank);
      }
      productService.updateFetchInfoStatus(productId, "updated");
    }
  }

  @Override
  public ProductDAO crossEcProduct(String modelNo) throws IOException {
    TrafficWebClient webClient = new TrafficWebClient(0, false);

    YahooProductDetailCrawler crawler = new YahooProductDetailCrawler(getECName(), webpageService);
    YahooProductDetailCrawlerResult crawlerResult = crawler.searchProductAndFetchProductInfoByModelNo(webClient, modelNo, true);
    webClient.finishTraffic();

    ProductInfo productInfo = Objects.isNull(crawlerResult) ? null : crawlerResult.getProductInfo();

    if (Objects.isNull(productInfo) || productInfo.getModelNo() == null) {
      LOGGER.warn("Unable to obtain cross ec product information for: " + modelNo);
      return null;
    }

    return new ProductDAO(getECName(), productInfo);
  }

}
