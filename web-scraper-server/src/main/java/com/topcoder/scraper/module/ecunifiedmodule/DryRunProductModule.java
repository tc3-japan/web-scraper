package com.topcoder.scraper.module.ecunifiedmodule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.IProductModule;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductCrawlerResult;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;

/**
 * General implementation of ProductDetailModule
 */
@Component
public class DryRunProductModule implements IProductModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(DryRunProductModule.class);

  // private final AmazonProperty property;
  private final ProductService productService;
  private final WebpageService webpageService;
  private GeneralProductCrawler crawler;

  private List<ProductInfo> productInfoList;
  private List<String> htmlPathList;

  @Autowired
  public DryRunProductModule(
      // AmazonProperty property,
      ProductService productService, WebpageService webpageService) {
    // this.property = property;
    this.productService = productService;
    this.webpageService = webpageService;
  }

  @Override
  public String getModuleType() {
    return "dryrun";
  }

  @Override
  public void fetchProductDetailList(List<String> sites) {
    LOGGER.debug("[fetchProductDetailList] in");
    LOGGER.debug("[fetchProductDetailList] sites:" + sites);

    this.productInfoList = new ArrayList<ProductInfo>();
    this.htmlPathList = new ArrayList<String>();

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
   * Fetch product information
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
    this.productInfoList.add(crawlerResult.getProductInfo());
    this.htmlPathList.add(crawlerResult.getHtmlPath());
    LOGGER.info(String.format("Add html file path:", crawlerResult.getHtmlPath()));
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

  public List<ProductInfo> getProductInfoList() {
    return this.productInfoList;
  }

  public List<String> getHtmlPathList() {
    // change path from abstract to relative that start at [logs] folder
    for (int i = 0; i < htmlPathList.size(); i++) {
      String htmlPath = htmlPathList.get(i);
      int delimiterIntex = htmlPath.lastIndexOf("logs");
      // number 4 means "logs" charactor count
      htmlPathList.set(i, "html" + htmlPath.substring(delimiterIntex + 4, htmlPath.length()));
    }
    return this.htmlPathList;
  }

}
