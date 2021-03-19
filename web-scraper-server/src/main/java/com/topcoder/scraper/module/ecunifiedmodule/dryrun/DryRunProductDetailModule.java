package com.topcoder.scraper.module.ecunifiedmodule.dryrun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.topcoder.common.util.Common;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.repository.ProductRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.traffic.TrafficWebClient.TrafficWebClientForDryRun;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductDetailCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductDetailCrawlerResult;
import com.topcoder.scraper.service.WebpageService;

/**
 * Dry run of ProductModule
 */
@Component
public class DryRunProductDetailModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(DryRunProductDetailModule.class);

    private final WebpageService webpageService;
    private GeneralProductDetailCrawler crawler;
    private TrafficWebClientForDryRun webClientDryRun;
    private List<ProductInfo> productInfoList;
    private List<String> htmlPathList;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    public DryRunProductDetailModule(WebpageService webpageService) {
        this.webpageService = webpageService;
    }

    public List<Object> fetchProductDetailList(String site, String conf, Integer count) {
        LOGGER.debug("[fetchProductDetailList] in");
        LOGGER.debug("[fetchProductDetailList] site:" + site);
        this.productInfoList = new ArrayList<ProductInfo>();
        this.htmlPathList = new ArrayList<String>();
        TrafficWebClient webClient = new TrafficWebClient(0, false);
        this.webClientDryRun = webClient.new TrafficWebClientForDryRun(0, false);
        this.crawler = new GeneralProductDetailCrawler(site, "product", this.webpageService, this.configurationRepository);
        crawler.setConfig(conf);
        DryRunUtils dru = new DryRunUtils(count);
        List<ProductDAO> products = productRepository.findByECSite(site);
        for (ProductDAO product : products) {
            try {
                this.processProductDetail(product.getId(), product.getProductCode());
            } catch (IOException | IllegalStateException e) {
                String message = String.format("Fail to fetch product %s, please try again.", product.getProductCode());
                Common.ZabbixLog(LOGGER, message, e);
            }
            if (dru.checkCountOver(productInfoList)) {
                break;
            }
        }
        return dru.toJsonOfDryRunProductModule(this.productInfoList, this.htmlPathList);
    }

    private void processProductDetail(int productId, String productCode) throws IOException {
        if (StringUtils.isBlank(productCode)) {
            LOGGER.info(String.format("Skipping Product#%d - no product code", productId));
            return;
        }
        GeneralProductDetailCrawlerResult crawlerResult = this.fetchProductDetail(productCode);
        this.productInfoList.add(crawlerResult.getProductInfo());
        this.htmlPathList.add(crawlerResult.getHtmlPath());
        LOGGER.info(String.format("Add html file path:", crawlerResult.getHtmlPath()));
    }

    private GeneralProductDetailCrawlerResult fetchProductDetail(String productCode) throws IOException {
        return this.crawler.fetchProductInfo(this.webClientDryRun, productCode);
    }

}
