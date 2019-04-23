package com.topcoder.scraper.module.kojima;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ProductDetailModule;
import com.topcoder.scraper.module.kojima.crawler.KojimaProductDetailCrawler;
import com.topcoder.scraper.module.kojima.crawler.KojimaProductDetailCrawlerResult;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;

@Component
public class KojimaProductDetailModule extends ProductDetailModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaProductDetailModule.class);

  private final TrafficWebClient webClient;
  private final ProductService productService;
  private final WebpageService webpageService;

  @Autowired
  public KojimaProductDetailModule(ProductService productService, WebpageService webpageService) {
    this.webClient = new TrafficWebClient(0, false);
    this.productService = productService;
    this.webpageService = webpageService;
  }
  
  @Override
  public String getECName() {
    return "kojima";
  }

  @Override
  public void fetchProductDetailList() throws IOException {
    List<ProductDAO> products = this.productService.getAllFetchInfoStatusIsNull(getECName());
    KojimaProductDetailCrawler crawler = new KojimaProductDetailCrawler(getECName(), webpageService);

    products.forEach(product -> {
      try {
        fetchProductDetail(crawler, product.getId(), product.getProductName()); // Kojima.net:  Product no. is not available in the history page.
      } catch (IOException | IllegalStateException e) {
        LOGGER.error(String.format("Fail to fetch product %s, please try again.", product.getProductCode()));
      }
    });
  }

  private void fetchProductDetail(KojimaProductDetailCrawler crawler, int productId, String productCode) throws IOException {
    KojimaProductDetailCrawlerResult crawlerResult = crawler.fetchProductInfo(webClient, productCode, false);
    ProductInfo productInfo = crawlerResult.getProductInfo();

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
}
