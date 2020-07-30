package com.topcoder.scraper.module.ecunifiedmodule.dryrun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.traffic.TrafficWebClient.TrafficWebClientForDryRun;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductSearchCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductSearchCrawlerResult;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;

/**
 * Dry run of ProductSearchModule
 */
@Component
public class DryRunProductSearchModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(DryRunProductSearchModule.class);

  private final ProductService productService;
  private final WebpageService webpageService;
  private GeneralProductSearchCrawler crawler;
  private TrafficWebClientForDryRun webClientDryRun;
  private List<String> productCodeList;
  private List<String> htmlPathList;

  @Autowired
  ConfigurationRepository configurationRepository;

  @Autowired
  public DryRunProductSearchModule(ProductService productService, WebpageService webpageService) {
    this.webpageService = webpageService;
    this.productService = productService;
  }

  public List<Object> searchProduct(String site, String conf) {
    LOGGER.debug("[searchProductInfoList] in");
    LOGGER.info(String.format("Searching products in %s. search-word: %s", site, DryRunUtils.SEARCH_KEYWORD));
    productCodeList = new ArrayList<String>();
    htmlPathList = new ArrayList<String>();
    TrafficWebClient webClient = new TrafficWebClient(0, false);
    this.webClientDryRun = webClient.new TrafficWebClientForDryRun(0, false);
    this.crawler = new GeneralProductSearchCrawler(site, "search", this.webpageService, this.configurationRepository);
    crawler.setConfig(conf);
    List<ProductDAO> products = this.productService.getAllFetchInfoStatusIsNull(site);
    products.forEach(product -> {
      try {
        if (product.getModelNo() != null) {
          this.searchProduct(product.getModelNo());
        }
        if (product.getJanCode() != null) {
          this.searchProduct(product.getJanCode());
        }
        if (product.getProductName() != null) {
          this.searchProduct(product.getProductName());
        }
      } catch (IOException | IllegalStateException e) {
        LOGGER.error(String.format("Fail to search product."));
      }
    });
    return new DryRunUtils().toJsonOfDryRunProductSearchModule(productCodeList, htmlPathList);
  }

  private void searchProduct(String searchWord) throws IOException {
    if (searchWord != null) {
      GeneralProductSearchCrawlerResult result = this.crawler.searchProduct(this.webClientDryRun, searchWord);
      if (Objects.nonNull(result)) {
        String productCode = result.getProductCode();
        if (Objects.isNull(productCode)) {
          productCode = "";
        }
        String htmlPath = result.getHtmlPath();
        if (Objects.isNull(htmlPath)) {
          htmlPath = "";
        }
        productCodeList.add(productCode);
        htmlPathList.add(htmlPath);
      }
    }
  }

}
