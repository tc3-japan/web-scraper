package com.topcoder.scraper.module.ecunifiedmodule.dryrun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.traffic.TrafficWebClient.TrafficWebClientForDryRun;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductCrawlerResult;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;

/**
 * Dry run of ProductSearchModule
 */
@Component
public class DryRunProductSearchModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(DryRunProductSearchModule.class);

  private final WebpageService webpageService;
  private final ProductService productService;
  private List<String> productCodeList;
  private List<String> htmlPathList;

  @Autowired
  public DryRunProductSearchModule(ProductService productService, WebpageService webpageService) {
    this.webpageService = webpageService;
    this.productService = productService;
  }

  public List<Object> searchProduct(String site) {
    LOGGER.debug("[searchProductInfoList] in");
    LOGGER.info(String.format("Searching products in %s. search-word: %s", site, DryRunUtils.SEARCH_KEYWORD));
    productCodeList = new ArrayList<String>();
    htmlPathList = new ArrayList<String>();
    TrafficWebClient webClient = new TrafficWebClient(0, false);
    TrafficWebClientForDryRun webClientDryRun = webClient.new TrafficWebClientForDryRun(0, false);
    GeneralProductCrawler crawler = new GeneralProductCrawler(site, this.webpageService);
    List<ProductDAO> products = this.productService.getAllFetchInfoStatusIsNull(site);
    products.forEach(product -> {
      try {
        if (product.getModelNo() != null) {
          this.searchProduct(site, product.getModelNo(), webClientDryRun, crawler);
        }
        if (product.getJanCode() != null) {
          this.searchProduct(site, product.getJanCode(), webClientDryRun, crawler);
        }
        if (product.getProductName() != null) {
          this.searchProduct(site, product.getProductName(), webClientDryRun, crawler);
        }
      } catch (IOException | IllegalStateException e) {
        LOGGER.error(String.format("Fail to search product."));
      }
    });
    return new DryRunUtils().toJsonOfDryRunProductSearchModule(productCodeList, htmlPathList);
  }

  private void searchProduct(String site, String keyword, TrafficWebClient webClient, GeneralProductCrawler crawler) throws IOException {
    if (keyword != null) {
      GeneralProductCrawlerResult result = crawler.searchProduct(webClient, keyword);
      String productCode = result.getProductCode();
      if (productCode == null) {
        productCode = "";
      }
      String htmlPath = result.getHtmlPath();
      if (htmlPath == null) {
        htmlPath = "";
      }
      productCodeList.add(productCode);
      htmlPathList.add(htmlPath);
    }
  }

}
