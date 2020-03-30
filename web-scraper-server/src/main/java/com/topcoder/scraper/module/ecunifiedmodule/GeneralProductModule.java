package com.topcoder.scraper.module.ecunifiedmodule;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.IProductModule;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductCrawlerResult;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * General implementation of ProductDetailModule
 */
@Component
public class GeneralProductModule implements IProductModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralProductModule.class);

  // private final AmazonProperty property;
  private final ProductService productService;
  private final WebpageService webpageService;
  private GeneralProductCrawler crawler;

  @Autowired
  public GeneralProductModule(
      // AmazonProperty property,
      ProductService productService, WebpageService webpageService) {
    // this.property = property;
    this.productService = productService;
    this.webpageService = webpageService;
  }

  @Override
  public String getModuleType() {
    return "general";
  }

  @Override
  public void fetchProductDetailList(List<String> sites) {
    LOGGER.debug("[fetchProductDetailList] in");
    LOGGER.debug("[fetchProductDetailList] sites:" + sites);

    for (String site : sites) {
      List<ProductDAO> products = this.productService.getAllFetchInfoStatusIsNull(site);

      products.forEach(product -> {
        try {
          this.processProductDetail(site, product.getId(), product.getProductCode());
        } catch (IOException | IllegalStateException e) {
          LOGGER.error(String.format("Fail to fetch product %s, please try again.", product.getProductCode()));
        }
      });
    }
  }

  /**
   * Fetch product information from yahoo and save in database
   *
   * @param productId   the product id
   * @param productCode the product code
   * @throws IOException webclient exception
   */
  private void processProductDetail(String site, int productId, String productCode) throws IOException {
    if (StringUtils.isBlank(productCode)) {
      LOGGER.info(String.format("Skipping Product#%d - no product code", productId));
      return;
    }
    GeneralProductCrawlerResult crawlerResult = this.fetchProductDetail(site, productCode);
    ProductInfo productInfo = crawlerResult.getProductInfo();

    if (productInfo != null) {
      // save updated information
      productService.updateProduct(productId, productInfo);
      for (int i = 0; i < productInfo.getCategoryList().size(); i++) {
        String category = productInfo.getCategoryList().get(i);
        Integer rank    = productInfo.getRankingList().get(i);
        productService.addCategoryRanking(productId, category, rank);
      }
      productService.updateFetchInfoStatus(productId, "updated");
    }
  }

  public GeneralProductCrawlerResult fetchProductDetail(String site, String productCode) throws IOException {
    this.crawler = new GeneralProductCrawler(site, webpageService);

    TrafficWebClient webClient = new TrafficWebClient(0, false);
    GeneralProductCrawlerResult crawlerResult = this.crawler.fetchProductInfo(webClient, productCode);
    webClient.finishTraffic();
    return crawlerResult;
  }

  @Override
  public ProductDAO searchProductInfo(String siteName, String searchKey) throws IOException {
    LOGGER.debug("[searchProductInfo] in");

    LOGGER.info(String.format("Searching products in %s. search-word: %s", siteName, searchKey));

    TrafficWebClient webClient = new TrafficWebClient(0, false);

    GeneralProductCrawler crawler = new GeneralProductCrawler(siteName, this.webpageService);
    String productCode = crawler.searchProduct(webClient, searchKey);

    ProductInfo productInfo = Objects.isNull(productCode) ? null : crawler.fetchProductInfo(webClient, productCode).getProductInfo();
    webClient.finishTraffic();

    if (Objects.isNull(productInfo)) {
      LOGGER.warn("[searchProductInfo] Unable to obtain a product information about: " + searchKey);
      return null;
    }

    return new ProductDAO(siteName, productInfo);
  }
}
