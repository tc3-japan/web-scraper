package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import com.gargoylesoftware.htmlunit.html.DomElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.scraper.ProductConfig;
import com.topcoder.common.model.scraper.ProductDetail;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.lib.navpage.NavigableProductDetailPage;
import com.topcoder.scraper.service.WebpageService;

public class GeneralProductDetailCrawler extends AbstractGeneralCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralProductDetailCrawler.class);

    public GeneralProductDetailCrawler(String site, String type, WebpageService webpageService, ConfigurationRepository configurationRepository) {
        super(site, type, webpageService, configurationRepository);
    }

    public GeneralProductDetailCrawlerResult fetchProductInfo(TrafficWebClient webClient, String productCode) throws IOException {
        LOGGER.debug("[fetchProductInfo] in");
        LOGGER.debug(String.format("param productCode=[%s]", productCode));
        productConfig = new ObjectMapper().readValue(jsonConfigText, ProductConfig.class);
        String url = productConfig.getUrl().replace("{code}", productCode);
        LOGGER.debug("url=" + url);
        ProductInfo productInfo = new ProductInfo();
        productInfo.setCode(productCode);
        NavigableProductDetailPage detailPage = new NavigableProductDetailPage(url, webClient, productInfo);
        for (List<ProductDetail> productDetails : productConfig.getProductDetails()) {
            for (ProductDetail productDetail : productDetails) {
                if (fetchProductInfo(url, productDetail, detailPage)) break;
            }
        }
        String json = detailPage.getProductInfo().toJson();
        LOGGER.debug("product detail json=" + json);

        String savedPath;
        if (site.equals("rakuten")) {
            DomElement de = detailPage.getPage().querySelector("body > script[src*=\"pa3\"]");
            if (de != null) de.remove();
            String html = detailPage.getPage().asXml().replace("<?xml version=\"1.0\" encoding=\"EUC-JP\"?>","");
            savedPath = detailPage.savePage(site, "product-detail", productCode, html, webpageService);
        } else {
            savedPath = detailPage.savePage(site, "product-detail", productCode, detailPage, webpageService);
        }

        return new GeneralProductDetailCrawlerResult(productInfo, savedPath);
    }

    private boolean fetchProductInfo(String url, ProductDetail productDetail, NavigableProductDetailPage naviPage) {
        ProductInfo productInfo = naviPage.getProductInfo();
        Field[] fields = ProductInfo.class.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(JsonProperty.class)) continue;

            String jsonPropertyValue = field.getAnnotation(JsonProperty.class).value();
            if (!Objects.equals(productDetail.getItem().replace("_label", ""), jsonPropertyValue)) continue;

            String scrapedResult = naviPage.fetchScrapedResultAsString(url, productDetail, jsonPropertyValue);
            if (StringUtils.isEmpty(scrapedResult)) return false;

            field.setAccessible(true);
            try {
                field.set(productInfo, scrapedResult);
                LOGGER.debug("the value [" + scrapedResult + "] was successfully set to field [" + field.getName() + "] of ProductInfo");
                return true;
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
                return false;
            }
        }

        return false;
    }

}
