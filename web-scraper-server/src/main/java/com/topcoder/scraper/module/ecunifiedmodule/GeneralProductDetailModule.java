package com.topcoder.scraper.module.ecunifiedmodule;

import java.io.IOException;
import java.util.List;
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
import com.topcoder.scraper.module.IProductDetailModule;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductDetailCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductDetailCrawlerResult;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;

/**
 * General implementation of ProductDetailModule
 */
@Component
public class GeneralProductDetailModule implements IProductDetailModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralProductDetailModule.class);

    private final ProductService productService;
    private final WebpageService webpageService;
    private GeneralProductDetailCrawler crawler;
    private TrafficWebClient webClient;

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    public GeneralProductDetailModule(ProductService productService, WebpageService webpageService) {
        this.productService = productService;
        this.webpageService = webpageService;
    }

    @Override
    public String getModuleType() {
        return "general";
    }

    @Override
    public void fetchProductDetailList(List<String> sites) {
        LOGGER.debug("[fetchProductDetailList] in");
        LOGGER.debug("[fetchProductDetailList] sites:" + sites);
        this.webClient = new TrafficWebClient(0, false);
        for (String site : sites) {
            this.crawler = new GeneralProductDetailCrawler(site, "product", this.webpageService, this.configurationRepository);
            List<ProductDAO> products = this.productService.getAllFetchInfoStatusIsNull(site);
            products.forEach(product -> {
                try {
                    this.processProductDetail(site, product.getId(), product.getProductCode());
                } catch (IOException | IllegalStateException e) {
                    LOGGER.error(String.format("Fail to fetch product %s, please try again.", product.getProductCode()));
                    e.printStackTrace();
                }
            });
        }
        this.webClient.finishTraffic();
    }

    private void processProductDetail(String site, int productId, String productCode) throws IOException {
        if (StringUtils.isBlank(productCode)) {
            LOGGER.info(String.format("Skipping Product#%d - no product code", productId));
            return;
        }
        GeneralProductDetailCrawlerResult crawlerResult = this.fetchProductDetail(site, productCode);
        ProductInfo productInfo = crawlerResult.getProductInfo();
        if (Objects.nonNull(productInfo)) {
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

    public GeneralProductDetailCrawlerResult fetchProductDetail(String site, String productCode) throws IOException {
        return this.crawler.fetchProductInfo(webClient, productCode);
    }

}
