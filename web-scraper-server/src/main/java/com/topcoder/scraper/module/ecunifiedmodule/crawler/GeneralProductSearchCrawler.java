package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.scraper.Consts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.topcoder.common.model.scraper.ProductSearchConfig;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.HtmlUtils;
import com.topcoder.scraper.lib.navpage.NavigableProductListPage;
import com.topcoder.scraper.service.WebpageService;

public class GeneralProductSearchCrawler extends AbstractGeneralCrawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralProductSearchCrawler.class);

    public GeneralProductSearchCrawler(String site, String type, WebpageService webpageService, ConfigurationRepository configurationRepository) {
        super(site, type, webpageService, configurationRepository);
    }

    public GeneralProductSearchCrawlerResult searchProduct(TrafficWebClient webClient, String searchWord) throws IOException {
        LOGGER.debug("[searchProduct] in");
        LOGGER.debug(String.format("param search word=[%s]", searchWord));
        NavigableProductListPage listPage = new NavigableProductListPage(webClient);
        ProductSearchConfig productSearchConfig = new ObjectMapper().readValue(jsonConfigText, ProductSearchConfig.class);
        String url = productSearchConfig.getUrl().replace("{word}", searchWord);
        String groupSelector = productSearchConfig.getGroupSelector();

        if (StringUtils.isEmpty(url)) {
            LOGGER.debug("[url] not found");
            return null;
        }
        if (StringUtils.isEmpty(groupSelector)) {
            LOGGER.debug("[groupSelector] not found");
            return null;
        }

        listPage.setPage(url);
        LOGGER.debug(String.format("[url]=[%s]", url));

        String productCode = null;
        int i = 0;
        Map<String, Integer> placeHolderNos = new HashMap<>();
        HtmlPage page = listPage.getPage();

        for (DomNode parent : page.querySelectorAll(groupSelector)) {
            productCode = null;
            if (i++ >= Consts.SEARCH_PRODUCT_TRIAL_COUNT) return null;
            placeHolderNos.put("productIndex", i);

            productCode = listPage.scrapeString(page, parent, productSearchConfig, placeHolderNos);
            if (StringUtils.isNotEmpty(productCode)) break;
        }

        // for rakuten
        if (!StringUtils.isEmpty(productCode)) {
            productCode = productCode.replace("%2F", "/");
        }

        LOGGER.debug(String.format("return product_code=[%s]", productCode));
        String savedPath = listPage.savePage(site, "product-list", searchWord, listPage, webpageService);
        return new GeneralProductSearchCrawlerResult(productCode, savedPath);
    }

}
