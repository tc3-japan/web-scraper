package com.topcoder.scraper.module.ecunifiedmodule;

import java.io.IOException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.IProductSearchModule;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductDetailCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductSearchCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductSearchCrawlerResult;
import com.topcoder.scraper.service.WebpageService;

/**
 * General implementation of ProductDetailModule
 */
@Component
public class GeneralProductSearchModule implements IProductSearchModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralProductSearchModule.class);

    private final WebpageService webpageService;
    private TrafficWebClient webClient;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    public GeneralProductSearchModule(WebpageService webpageService) {
        this.webpageService = webpageService;
    }

    @Override
    public String getModuleType() {
        return "general";
    }

    @Override
    public ProductDAO searchProductInfo(String siteName, String searchWord) throws IOException {
        LOGGER.debug("[searchProductInfo] in");
        LOGGER.info(String.format("Searching products in %s. search-word: %s", siteName, searchWord));
        this.webClient = new TrafficWebClient(0, false);
        GeneralProductSearchCrawler searchCrawler = new GeneralProductSearchCrawler(siteName, "search", this.webpageService, this.configurationRepository);
        String productCode = "";
        GeneralProductSearchCrawlerResult searchCrawlerResult = searchCrawler.searchProduct(this.webClient, searchWord);
        if (Objects.isNull(searchCrawlerResult)) {
            this.webClient.finishTraffic();
            return null;
        } else {
            productCode = searchCrawlerResult.getProductCode();
            if (StringUtils.isEmpty(productCode)) {
                this.webClient.finishTraffic();
                return null;
            }
        }
        GeneralProductDetailCrawler detailCrawler = new GeneralProductDetailCrawler(siteName, "product", this.webpageService, this.configurationRepository);
        ProductInfo productInfo = detailCrawler.fetchProductInfo(this.webClient, productCode).getProductInfo();
        if (Objects.isNull(productInfo)) {
            LOGGER.warn("[searchProductInfo] Unable to obtain a product information about: " + searchWord);
            this.webClient.finishTraffic();
            return null;
        }
        this.webClient.finishTraffic();
        return new ProductDAO(siteName, productInfo);
    }
}
