package com.topcoder.scraper.module.ecisolatedmodule.kojima;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.IProductDetailModule;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductDetailCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductDetailCrawlerResult;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;

@Component
public class KojimaProductDetailModule extends IProductDetailModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaProductDetailModule.class);

  private final ProductService productService;
  private final WebpageService webpageService;

  @Autowired
  public KojimaProductDetailModule(ProductService productService, WebpageService webpageService) {
    this.productService = productService;
    this.webpageService = webpageService;
  }
  
  @Override
  public String getECName() {
    return "kojima";
  }

  @Override
  public void fetchProductDetailList(List<String> sites) throws IOException {
    LOGGER.info("---fetchProductDetailList------------------------------------------------");
    LOGGER.info("sites:" + sites);
    LOGGER.info("---fetchProductDetailList------------------------------------------------");

    List<ProductDAO> products = this.productService.getAllFetchInfoStatusIsNull(getECName());
    GeneralProductDetailCrawler crawler = new GeneralProductDetailCrawler(getECName(), webpageService);
    
    products.forEach(product -> {
      try {
        fetchProductDetail(crawler, product.getId(), product.getProductCode());
      } catch (IOException | IllegalStateException e) {
        LOGGER.error(String.format("Fail to fetch product %s, please try again.", product.getProductCode()));
      }
    });
  }

  private void fetchProductDetail(GeneralProductDetailCrawler crawler, int productId, String productName) throws IOException {
    TrafficWebClient webClient = new TrafficWebClient(0, false);

    GeneralProductDetailCrawlerResult crawlerResult = crawler.fetchProductInfo(webClient, productName);
    webClient.finishTraffic();
    ProductInfo productInfo = crawlerResult != null ? crawlerResult.getProductInfo() : null;
    
    if (productInfo == null) {
      LOGGER.warn("Unable to obtain a detailed information about: " + productName);
      return;
    }

    // save updated information
    productService.updateProduct(productId, productInfo);
    /* Kojima: Category and Ranking are not available
    for (int i = 0; i < productInfo.getCategoryList().size(); i++) {
      String category = productInfo.getCategoryList().get(i);
      Integer rank = productInfo.getRankingList().get(i);
      productService.addCategoryRanking(productId, category, rank);
    }
    */
    productService.updateFetchInfoStatus(productId, "updated");
  }

  @Override
  public ProductDAO crossEcProduct(String modelNo) throws IOException {
    TrafficWebClient webClient = new TrafficWebClient(0, false);

    GeneralProductDetailCrawler crawler = new GeneralProductDetailCrawler(getECName(), webpageService);
	  GeneralProductDetailCrawlerResult crawlerResult = crawler.fetchProductInfo(webClient, modelNo);
	  webClient.finishTraffic();
	  ProductInfo productInfo = Objects.isNull(crawlerResult) ? null : crawlerResult.getProductInfo();
	    
	  if (Objects.isNull(productInfo)) {
	    LOGGER.warn("Unable to obtain a cross ec product information about: " + modelNo);
	    return null;
	  }
	  
	  return new ProductDAO(getECName(), productInfo);
  }
}
