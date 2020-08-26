package com.topcoder.scraper.lib.navpage;

import java.util.Map;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.html.*;
import com.topcoder.common.model.scraper.ProductSearchConfig;
import com.topcoder.common.model.scraper.Selector;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.HtmlUtils;

import static com.topcoder.common.util.HtmlUtils.extract1;
import static com.topcoder.common.util.HtmlUtils.getTextContent;

public class NavigableProductListPage extends NavigablePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigableProductListPage.class);

    public NavigableProductListPage(TrafficWebClient webClient) {
        super((HtmlPage) null, webClient);
        LOGGER.debug("[constructor] in");
    }

    /**
     * scrape value by sector
     *
     * @param root                the page root
     * @param parent              the parent node
     * @param productSearchConfig the productSearchConfig object
     * @param placeHolderNos      the placeholder no
     * @return final value
     */
    public String scrapeString(HtmlPage root, DomNode parent, ProductSearchConfig productSearchConfig, Map<String, Integer> placeHolderNos) {
        HtmlElement element;
        if (productSearchConfig == null) {
            return null;
        }
        if (isValid(productSearchConfig.getIsScript())) {
            String script = productSearchConfig.getScript();
            return executeJavaScript(root, script, placeHolderNos);
        }

        if (isValid(productSearchConfig.getExcludedSelector())) {
            element = parent.querySelector(productSearchConfig.getExcludedSelector());
            if (element != null) {
                return null;
            }
        }

        element = parent.querySelector(productSearchConfig.getSelector());
        if (element == null) {
            return null;
        }
        String content = getTextContent(element);
        if (isValid(productSearchConfig.getAttribute())) {
            content = element.getAttribute(productSearchConfig.getAttribute());
        }
        if (isValid(productSearchConfig.getRegex())) {
            content = extract1(content, Pattern.compile(productSearchConfig.getRegex()));
        }
        return content;
    }

    public void searchProductsUsingForm(String searchUrl, String searchFormName, String searchInputName, String searchButtonSelector, String searchWord) {
        LOGGER.debug("[searchProductsUsingForm] in");

        try {
            HtmlPage topPage = this.webClient.getWebClient().getPage(searchUrl);
            HtmlForm searchForm = topPage.getFormByName(searchFormName);

            HtmlTextInput searchInput = searchForm.getInputByName(searchInputName);
            searchInput.type(searchWord);
            HtmlImageInput searchButtonInput = topPage.querySelector(searchButtonSelector);

            this.page = webClient.click(searchButtonInput);
        } catch (Exception e) {
            LOGGER.debug("[searchProductsUsingForm] failed to get products. page: " + searchUrl + "search word:" + searchWord, e);
        }
    }

    public String scrapeProductCodeFromSearchResultByProductAttrName(String searchWord, String searchResultSelector, String productCodeAttribute, String adProductClass, String productCodeRegex) {
        LOGGER.debug("[scrapeProductCodeFromSearchResultByProductAttrName] in");

        HtmlElement element = this.page.querySelector(searchResultSelector);
        if (element == null) {
            return null;
        }
        //skip ad product
        if (StringUtils.isNotEmpty(adProductClass)) {
            if (element.getAttribute("class").contains(adProductClass)) {
                LOGGER.info(String.format("Skip ad product with search word = %s", searchWord));
                return null;
            }
        }
        // get product code (amazon: data-asin, yahoo: data-beacon)
        String productCode = element.getAttribute(productCodeAttribute);
        // extract product code using regex (yahoo)
        if (StringUtils.isNotEmpty(productCode) && StringUtils.isNotEmpty(productCodeRegex)) {
            productCode = HtmlUtils.extract1(productCode, Pattern.compile(productCodeRegex));
        }
        if (StringUtils.isEmpty(productCode)) {
            return null;
        }

        LOGGER.info(String.format("Product is found with search word = %s, product code is %s", searchWord, productCode));
        return productCode;
    }

    public String scrapeProductCodeFromSearchResultByProductUrl(String searchWord, String searchResultSelector, String productCodeRegex) {
        LOGGER.debug("[scrapeProductCodeFromSearchResultByProductUrl] in");

        HtmlAnchor anchor = this.page.querySelector(searchResultSelector);
        if (anchor == null) {
            return null;
        }
        //get product code
        String hrefStr = anchor.getHrefAttribute();
        String productCode = HtmlUtils.extract1(hrefStr, Pattern.compile(productCodeRegex));
        if (productCode == null) {
            return null;
        }
        LOGGER.info(String.format("Product is found with search word = %s, product code is %s", searchWord, productCode));
        return productCode;
    }
}
