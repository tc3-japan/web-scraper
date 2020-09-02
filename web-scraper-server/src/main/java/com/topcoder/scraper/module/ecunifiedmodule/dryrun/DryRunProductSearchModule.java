package com.topcoder.scraper.module.ecunifiedmodule.dryrun;

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
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.repository.ProductRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.traffic.TrafficWebClient.TrafficWebClientForDryRun;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductSearchCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductSearchCrawlerResult;
import com.topcoder.scraper.service.WebpageService;

/**
 * Dry run of ProductSearchModule
 */
@Component
public class DryRunProductSearchModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(DryRunProductSearchModule.class);

    private final WebpageService webpageService;
    private GeneralProductSearchCrawler crawler;
    private TrafficWebClientForDryRun webClientDryRun;
    private List<String> productCodeList;
    private List<String> htmlPathList;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    public DryRunProductSearchModule(WebpageService webpageService) {
        this.webpageService = webpageService;
    }

    public List<Object> searchProduct(String site, String conf, Integer count) {
        LOGGER.debug("[searchProductInfoList] in");
        LOGGER.info(String.format("Searching products in %s.", site));
        productCodeList = new ArrayList<String>();
        htmlPathList = new ArrayList<String>();
        TrafficWebClient webClient = new TrafficWebClient(0, false);
        this.webClientDryRun = webClient.new TrafficWebClientForDryRun(0, false);
        this.crawler = new GeneralProductSearchCrawler(site, "search", this.webpageService, this.configurationRepository);
        crawler.setConfig(conf);
        DryRunUtils dru = new DryRunUtils(count);
        List<ProductDAO> products = productRepository.findByECSite(site);
        for (ProductDAO product : products) {
            try {
                if (product.getModelNo() != null) {
                    LOGGER.info(String.format("Search product by model no :%s.", product.getModelNo()));
                    this.searchProduct(product.getModelNo());
                    if (dru.checkCountOver(productCodeList)) break;
                }
                if (product.getJanCode() != null) {
                    LOGGER.info(String.format("Search product by jan code :%s.", product.getJanCode()));
                    this.searchProduct(product.getJanCode());
                    if (dru.checkCountOver(productCodeList)) break;
                }
                if (product.getProductName() != null) {
                    LOGGER.info(String.format("Search product by product name :%s.", product.getProductName()));
                    this.searchProduct(product.getProductName());
                    if (dru.checkCountOver(productCodeList)) break;
                }
            } catch (IOException | IllegalStateException e) {
                LOGGER.error(String.format("Fail to search product."));
            }
        }
        ;
        return dru.toJsonOfDryRunProductSearchModule(productCodeList, htmlPathList);
    }

    private void searchProduct(String searchWord) throws IOException {
        if (searchWord != null) {
            GeneralProductSearchCrawlerResult result = this.crawler.searchProduct(this.webClientDryRun, searchWord);
            if (Objects.nonNull(result)) {
                String productCode = result.getProductCode();
                if (StringUtils.isEmpty(productCode)) {
                    return;
                }
                String htmlPath = result.getHtmlPath();
                if (StringUtils.isEmpty(htmlPath)) {
                    return;
                }
                productCodeList.add(productCode);
                htmlPathList.add(htmlPath);
            }
        }
    }

}
