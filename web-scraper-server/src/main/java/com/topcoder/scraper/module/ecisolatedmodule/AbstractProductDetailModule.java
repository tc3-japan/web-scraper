package com.topcoder.scraper.module.ecisolatedmodule;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.IProductDetailModule;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractProductDetailCrawler;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractProductDetailCrawlerResult;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public abstract class AbstractProductDetailModule implements IProductDetailModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProductDetailModule.class);

  protected final ProductService productService;
  protected final WebpageService webpageService;
  protected final AbstractProductDetailCrawler crawler;

  public AbstractProductDetailModule(ProductService productService, WebpageService webpageService, AbstractProductDetailCrawler crawler) {
    this.productService = productService;
    this.webpageService = webpageService;
    this.crawler        = crawler;
  }

  @Override
  public abstract String getModuleType();

  //protected abstract AbstractProductDetailCrawler getCrawler();

  @Override
  public void fetchProductDetailList(List<String> sites) {
    List<ProductDAO> products = this.productService.getAllFetchInfoStatusIsNull(this.getModuleType());

    products.forEach(product -> {
      try {
        this.processProductDetail(product.getId(), product.getProductCode());
      } catch (IOException | IllegalStateException e) {
        LOGGER.error(String.format("Fail to fetch product %s, please try again.", product.getProductCode()));
      }
    });
  }

  /**
   * Fetch product information from yahoo and save in database
   * 
   * @param productId   the product id
   * @param productCode the product code
   * @throws IOException webclient exception
   */
  private void processProductDetail(int productId, String productCode) throws IOException {

    AbstractProductDetailCrawlerResult crawlerResult = this.fetchProductDetail(productCode);
    ProductInfo productInfo = crawlerResult.getProductInfo();

    if (productInfo != null) {
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

  public AbstractProductDetailCrawlerResult fetchProductDetail(String productCode) throws IOException {
    TrafficWebClient webClient = new TrafficWebClient(0, false);
    AbstractProductDetailCrawlerResult crawlerResult = this.crawler.fetchProductInfo(webClient, productCode);
    webClient.finishTraffic();
    return crawlerResult;
  }
  /*
  @Override
  //TODO: ADD String site parameter to this function in base class
  public ProductDAO crossEcProduct(String site, String modelNo) throws IOException {
    TrafficWebClient webClient = new TrafficWebClient(0, false);

    AbstractProductDetailCrawler crawler = new AbstractProductDetailCrawler(site, webpageService);
    AbstractProductDetailCrawlerResult crawlerResult = crawler.fetchProductInfo(webClient, modelNo);

    webClient.finishTraffic();

    ProductInfo productInfo = Objects.isNull(crawlerResult) ? null : crawlerResult.getProductInfo();

    if (Objects.isNull(productInfo) || productInfo.getModelNo() == null) {
      LOGGER.warn("Unable to obtain cross ec product information for: " + modelNo);
      return null;
    }

    return new ProductDAO(site, productInfo);
  }
  */

  @Override
  public ProductDAO crossEcProduct(String modelNo) throws IOException {
    // TODO Auto-generated method stub
    // FAKE CLASS DO NOT USE
    return null;
  }
}
