package com.topcoder.scraper.lib.navpage;

import static com.topcoder.common.util.HtmlUtils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.solr.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.scraper.ProductDetail;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.Common;
import com.topcoder.common.util.HtmlUtils;

import groovy.lang.Closure;
import lombok.Getter;
import lombok.Setter;

public class NavigableProductDetailPage extends NavigablePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigableProductDetailPage.class);

    // TrafficWebClient webClient;
    // HtmlPage page;
    @Getter
    @Setter
    private ProductInfo productInfo;

    public NavigableProductDetailPage(HtmlPage page, TrafficWebClient webClient, ProductInfo productInfo) {
        super(page, webClient);
        LOGGER.debug("[constructor] in");
        this.productInfo = productInfo;
    }

    public NavigableProductDetailPage(String url, TrafficWebClient webClient, ProductInfo productInfo) {
        super(url, webClient);
        LOGGER.debug("[constructor] in");
        this.productInfo = productInfo;
    }

    public NavigableProductDetailPage(TrafficWebClient webClient) {
        super((HtmlPage) null, webClient);
        LOGGER.debug("[constructor] in");
    }

    public String scrapeText(String selector) {
        LOGGER.debug("[scrapeText] in");
        String str = getText(selector);
        LOGGER.debug("[scrapeText] text >>>> " + str);
        return str;
    }

    public void scrapeDistributor(String selector) {
        LOGGER.debug("[scrapeDistributor] in");
        String str = getText(selector);
        LOGGER.debug("[scrapeDistributor] Distributor >>>> " + str);
        if (str != null) {
            productInfo.setDistributor(str);
        }
    }

    public void scrapeDistributor(DomNode node, String selector) {
        LOGGER.debug("[scrapeDistributor] in");
        String str = getText(node, selector);
        LOGGER.debug("[scrapeDistributor] Distributor >>>> " + str);
        if (str != null) {
            productInfo.setDistributor(str);
        }
    }

    public void scrapeCode(String selector) {
        LOGGER.debug("[scrapeCode] in");
        String code = getText(selector);
        LOGGER.debug("[scrapeCode] Code >>>> " + code);
        if (code != null) {
            productInfo.setCode(code);
        }
    }

    public void scrapeCode(DomNode node, String selector) {
        LOGGER.debug("[scrapeCode] in");
        String code = getText(node, selector);
        LOGGER.debug("[scrapeCode] Code >>>> " + code);
        if (code != null) {
            productInfo.setCode(code);
        }
    }

    public void scrapeCodeFromAttr(String selector, String attrName, String codeRegexStr) {
        LOGGER.debug("[scrapeCodeFromAttr] in");
        HtmlElement node = this.page.querySelector(selector);
        String attrValue = node.getAttribute(attrName);
        Pattern pattern = Pattern.compile(codeRegexStr);

        String str = extract1(attrValue, pattern);
        LOGGER.debug("[scrapeCodeFromAttr] >>> Setting Product Code >>>" + str);
        if (str != null) {
            productInfo.setCode(str);
        }
    }

    public void scrapeName(String selector) {
        LOGGER.debug("[scrapeName] in");
        String str = getText(selector);
        LOGGER.debug("[scrapeName] Name >>>> " + str);
        if (str != null) {
            productInfo.setName(str);
        }
    }

    public void scrapeName(DomNode node, String selector) {
        LOGGER.debug("[scrapeName] in");
        String str = getText(node, selector);
        LOGGER.debug("[scrapeName] Name >>>> " + str);
        if (str != null) {
            productInfo.setName(str);
        }
    }

    public void scrapePrice(String selector) {
        LOGGER.debug("[scrapePrice] in");
        String str = getText(selector);
        LOGGER.debug("[scrapePrice] Price >>>> " + str);
        if (str != null) {
            productInfo.setPrice(str);
        }
    }

    public void scrapePrice(DomNode node, String selector) {
        LOGGER.debug("[scrapePrice] in");
        String str = getText(node, selector);
        LOGGER.debug("[scrapePrice] Price >>>> " + str);
        if (str != null) {
            productInfo.setPrice(str);
        }
    }

    public void scrapePrices(List<String> selectors) {
        LOGGER.debug("[scrapePrices] in");
        for (String selector : selectors) {
            String str = getText(selector);
            LOGGER.debug("[scrapePrices] Price >>>> " + str);
            if (str != null) {
                productInfo.setPrice(str);
                return;
            }
        }
    }

    public void scrapeJanCode(String selector) {
        LOGGER.debug("[scrapeJanCode] in");
        String str = getText(selector);
        LOGGER.debug("[scrapeJanCode] JAN Code >>>> " + str);
        if (str != null) {
            productInfo.setJanCode(str);
        }
    }

    public void scrapeJanCode(DomNode node, String selector) {
        LOGGER.debug("[scrapeJanCode] in");
        String str = getText(node, selector);
        LOGGER.debug("[scrapeJanCode] JAN Code >>>> " + str);
        if (str != null) {
            productInfo.setJanCode(str);
        }
    }

    public void scrapeModelNo(String selector) {
        LOGGER.debug("[scrapeModelNo] in");
        String str = Common.normalize(getText(selector));
        LOGGER.debug("[scrapeModelNo] Model No >>>> " + str);
        if (str != null) {
            productInfo.setModelNo(str);
        }
    }

    public void scrapeModelNo(DomNode node, String selector) {
        LOGGER.debug("[scrapeModelNo] in");
        String str = Common.normalize(getText(node, selector));
        LOGGER.debug("[scrapeModelNo] Model No >>>> " + str);
        if (str != null) {
            productInfo.setModelNo(str);
        }
    }

    public void scrapeModelNo(List<String> modelNoLabelNames, List<String> modelNoLabelSelectors, List<String> modelNoValueSelectors) {
        LOGGER.debug("[scrapeModelNo] in");
        HtmlElement modelNoLabelElement = null;
        HtmlElement modelNoValueElement = null;
        for (int i = 0; i < modelNoLabelSelectors.size(); i++) {
            modelNoLabelElement = this.page.querySelector(modelNoLabelSelectors.get(i));
            modelNoValueElement = this.page.querySelector(modelNoValueSelectors.get(i));

            if (modelNoLabelElement != null
                    && modelNoValueElement != null
                    && HtmlUtils.getTextContent(modelNoLabelElement).replaceAll("[:：]", "").equals(modelNoLabelNames.get(i))) {

                LOGGER.info(String.format("[scrapeModelNo] model no (%s) is found by selector: %s", modelNoLabelNames.get(i), modelNoValueSelectors.get(i)));
                String modelNo = Common.normalize(HtmlUtils.getTextContentWithoutDuplicatedSpaces(modelNoValueElement));
                this.productInfo.setModelNo(modelNo);
                break;
            }
        }
    }

    private static final String MODEL_NO_LABEL_NAME_KEY = "label_name";
    private static final String MODEL_NO_LABEL_SELECTOR_KEY = "label_selector";
    private static final String MODEL_NO_VALUE_SELECTOR_KEY = "value_selector";

    public void scrapeModelNo(List<Map<String, String>> modelNoSelectors) {
        LOGGER.debug("[scrapeModelNo] in");
        HtmlElement modelNoLabelElement = null;
        HtmlElement modelNoValueElement = null;
        for (Map<String, String> modelNoSelector : modelNoSelectors) {
            modelNoLabelElement = this.page.querySelector(modelNoSelector.get(MODEL_NO_LABEL_SELECTOR_KEY));
            modelNoValueElement = this.page.querySelector(modelNoSelector.get(MODEL_NO_VALUE_SELECTOR_KEY));

            if (modelNoLabelElement != null
                    && modelNoValueElement != null
                    && HtmlUtils.getTextContent(modelNoLabelElement).replaceAll("[:：]", "").equals(modelNoSelector.get(MODEL_NO_LABEL_NAME_KEY))) {

                LOGGER.info(String.format("[scrapeModelNo] model no (%s) is found by selector: %s", modelNoSelector.get(MODEL_NO_LABEL_NAME_KEY), modelNoSelector.get(MODEL_NO_VALUE_SELECTOR_KEY)));
                String modelNo = Common.normalize(HtmlUtils.getTextContentWithoutDuplicatedSpaces(modelNoValueElement));
                this.productInfo.setModelNo(modelNo);
                break;
            }
        }
    }

    public void scrapeQuantity(String selector) {
        LOGGER.debug("[scrapeQuantity] in");
        String str = getText(selector);
        LOGGER.debug("[scrapeQuantity] Quantity >>>> " + str);
        Integer qty = extractInt(str);
        if (str != null && qty != null) {
            // productInfo.setQuantity(extractInt(str));
            productInfo.setQuantity(qty);
        }
    }

    public void scrapeQuantity(DomNode node, String selector) {
        LOGGER.debug("[scrapeQuantity] in");
        String str = getText(node, selector);
        LOGGER.debug("[scrapeQuantity] Quantity >>>> " + str);
        if (str != null) {
            productInfo.setQuantity(extractInt(str));
        }
    }

    public void scrapeCategoryRanking(List<String> categoryInfoList) {
        LOGGER.debug("[scrapeCategoryRanking] in");

        if (categoryInfoList.size() <= 0) {
            LOGGER.info(String.format("Could not find category rankings for product %s", this.productInfo.getCode()));
            return;
        }

        for (String data : categoryInfoList) {
            // categoryInfo = [rank] [in] [category path]
            // in may contain ascii char number 160, so replace it with space
            String[] categoryInfo = data.replace("\u00A0", " ").split(" ", 3);

            // rank: remove possible leading # and comma, then convert to int
            int rank = Integer.valueOf(categoryInfo[0].replaceAll("[^0-9]*", ""));
            // path
            String path = categoryInfo[2];

            this.productInfo.addCategoryRanking(path, rank);
        }
    }

    // category ranking from li#salesrank is for scrapeCategoryRanking().
    public List<String> scrapeCategoryInfoListBySalesRank(String salesRankSelector, Closure<?> setProps) {
        LOGGER.debug("[scrapeCategoryInfoListBySalesRank] in");
        List<String> categoryInfoList = new ArrayList<>();

        // properties for scraping (1st_line_regex, rest_ranks_selector, rest_paths_selector)
        Map<String, String> props = new HashMap<>();
        setProps.call(props);

        DomNode node = this.page.getPage().querySelector(salesRankSelector);
        if (node != null) {

            // get first rank and category path
            Pattern pattern = Pattern.compile(props.get("1st_line_regex"));
            Matcher matcher = pattern.matcher(node.getTextContent());
            if (matcher.find()) {
                String firstRankAndPath = matcher.group(2).trim() + " - " + matcher.group(1).trim();
                categoryInfoList.add(firstRankAndPath);
            }

            // get rest of ranks and category paths
            List<DomNode> ranks = node.querySelectorAll(props.get("rest_ranks_selector"));
            List<DomNode> paths = node.querySelectorAll(props.get("rest_paths_selector"));
            for (int i = 0; i < ranks.size(); i++) {
                categoryInfoList.add(HtmlUtils.getTextContent((HtmlElement) ranks.get(i)) + " " + HtmlUtils.getTextContent((HtmlElement) paths.get(i)));
            }
        }
        return categoryInfoList;
    }

    // category ranking from product table is for scrapeCategoryRanking().
    public List<String> scrapeCategoryInfoListByProductInfoTable(String productInfoTableSelector, Closure<?> setProps, Closure<Boolean> rankLineTest) {
        LOGGER.debug("[scrapeCategoryInfoListByProductInfoTable] in");

        // properties for scraping (lines_selector, ranks_selector)
        Map<String, String> props = new HashMap<>();
        setProps.call(props);

        DomNode node = this.page.getPage().querySelector(productInfoTableSelector);
        if (node != null) {
            List<DomNode> trList = node.querySelectorAll(props.get("lines_selector"));
            for (DomNode tr : trList) {
                if (rankLineTest.call(tr)) {
                    //HtmlUtils.getTextContent(tr.querySelector("th")).contains("Rank");
                    List<DomNode> spanList = tr.querySelectorAll(props.get("ranks_selector"));
                    return spanList.stream().map(span -> HtmlUtils.getTextContent((HtmlElement) span)).collect(Collectors.toList());
                }
            }
        }
        return new ArrayList<>();
    }

    public String fetchScrapedResultAsString(String url, ProductDetail productDetail, String jsonPropertiyName) {
        String result = null;

        // javascript
        if (isValid(productDetail.getIsScript())) {
            return executeJavaScript(page, productDetail.getScript(), null);
        }

        // label of model_no
        if (isValid(productDetail.getLabelSelector())) {
            // scrape label
            String labelValue = scrapeText(productDetail.getLabelSelector());
            if (StringUtils.isEmpty(labelValue) || !labelValue.trim().equals(productDetail.getLabelValue().trim()))
                return null;
            // attribute of label
            if (isValid(productDetail.getLabelAttribute())) {
                result = getNodeAttribute(productDetail.getLabelSelector(), productDetail.getLabelAttribute());
                if (Objects.isNull(result)) {
                    return null;
                }
            }
            // regex of label
            String labelRegex = productDetail.getLabelRegex();
            if (isValid(labelRegex)) {
                LOGGER.debug(String.format("regex=[%s]", labelRegex));
                Pattern pattern = Pattern.compile(labelRegex);
                result = HtmlUtils.extract2(result, pattern);
                LOGGER.debug(String.format("regex result=[%s]", result));
                if (Objects.isNull(result)) {
                    return null;
                }
            }
        }

        // scrape target
        if (isValid(productDetail.getAttribute())) {
            // attribute
            result = getNodeAttribute(productDetail.getSelector(), productDetail.getAttribute());
        } else {
            result = scrapeText(productDetail.getSelector());
        }

        if (Objects.isNull(result)) {
            return null;
        }

        // regex
        String regex = productDetail.getRegex();
        if (isValid(regex)) {
            LOGGER.debug(String.format("regex=[%s]", regex));
            Pattern pattern = Pattern.compile(regex);
            result = HtmlUtils.extract2(result, pattern);
            LOGGER.debug(String.format("regex result=[%s]", result));
        }

        if (productDetail.getItem().equals("unit_price")) {
            Float price = extractFloat(result);
            result = price == null ? null : price.toString();
        }

        return result;
    }

}
