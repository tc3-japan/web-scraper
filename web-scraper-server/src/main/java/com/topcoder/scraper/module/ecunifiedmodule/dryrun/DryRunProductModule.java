package com.topcoder.scraper.module.ecunifiedmodule.dryrun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.traffic.TrafficWebClient.TrafficWebClientForDryRun;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductCrawlerResult;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;

/**
 * Dry run of ProductModule
 */
@Component
public class DryRunProductModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(DryRunProductModule.class);

  private final ProductService productService;
  private final WebpageService webpageService;
  private GeneralProductCrawler crawler;
  private List<ProductInfo> productInfoList;
  private List<String> htmlPathList;

  @Autowired
  public DryRunProductModule(ProductService productService, WebpageService webpageService) {
    this.productService = productService;
    this.webpageService = webpageService;
  }

  public List<Object> fetchProductDetailList(List<String> sites) {
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
    return new DryRunUtils().toJsonOfDryRunProductModule(this.productInfoList, this.htmlPathList);
  }

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

  private GeneralProductCrawlerResult fetchProductDetail(String site, String productCode) throws IOException {
    this.crawler = new GeneralProductCrawler(site, webpageService);
    TrafficWebClient webClient = new TrafficWebClient(0, false);
    TrafficWebClientForDryRun webClientDryRun = webClient.new TrafficWebClientForDryRun(0, false);
    GeneralProductCrawlerResult crawlerResult = this.crawler.fetchProductInfo(webClientDryRun, productCode);
    return crawlerResult;
  }

}
