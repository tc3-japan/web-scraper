package com.topcoder.scraper.module.general;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.topcoder.common.util.HtmlUtils;
import groovy.lang.Closure;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class ProductDetailCrawlerScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductDetailCrawlerScriptSupport.class);
  private static ProductDetailCrawler CRAWLER;

  static void setCrawler(ProductDetailCrawler crawler) {
    CRAWLER = crawler;
  }

  // TODO: move to more general class
  void navigatePage(String productUrl) {
    try {
      CRAWLER.productPage = CRAWLER.webClient.getPage(productUrl);
    } catch (IOException e) {
      LOGGER.info("Could not navigate: " + productUrl);
    }
  }

  void scrapePrice(List<String> priceSelectors) {
    // Pair includes element and it's selector string.
    Pair<HtmlElement, String> priceElementPair = HtmlUtils.findFirstElementInSelectors(CRAWLER.productPage, priceSelectors);
    if (priceElementPair == null) {
      LOGGER.info(String.format("Could not find price info for product %s:%s", CRAWLER.siteName, CRAWLER.productCode));
    } else {
      HtmlElement priceElement  = priceElementPair.getFirst();
      String      priceSelector = priceElementPair.getSecond();
      LOGGER.info("Price's found by selector: " + priceSelector);

      String price = HtmlUtils.getTextContentWithoutDuplicatedSpaces(priceElement);
      // special case handle, for example
      // https://www.amazon.com/gp/product/B016KBVBCS
      // current value of price is $ 75 55
      String[] priceArray = price.split(" ");
      if (priceArray.length == 3) {
        price = String.format("%s%s.%s", priceArray[0], priceArray[1], priceArray[2]);
      }

      CRAWLER.productInfo.setPrice(HtmlUtils.getNumberAsStringFrom(price));
    }
  }

  void scrapeName(String nameSelector) {
    HtmlElement nameElement = CRAWLER.productPage.querySelector(nameSelector);
    if (nameElement == null) {
      LOGGER.info(String.format("Could not find name info for product %s:%s", CRAWLER.siteName, CRAWLER.productCode));
    } else {
      String name = HtmlUtils.getTextContent(nameElement);
      CRAWLER.productInfo.setName(name);
    }
  }

  void scrapeModelNo(
          List<String> modelNoLabels,
          List<String> modelNoLabelValues,
          List<String> modelNoValues
  ) {

    HtmlElement modelLabelElement   = null;
    HtmlElement modelNoValueElement = null;
    for(int i = 0 ; i < modelNoLabels.size() ; i++) {
      modelLabelElement   = CRAWLER.productPage.querySelector(modelNoLabels.get(i));
      modelNoValueElement = CRAWLER.productPage.querySelector(modelNoValues.get(i));

      if (modelLabelElement != null
              && modelNoValueElement != null
              && HtmlUtils.getTextContent(modelLabelElement).replaceAll("[:：]", "").equals(modelNoLabelValues.get(i))) {

        LOGGER.info("model no is found by selector: " + modelNoValueElement);
        String modelNo = HtmlUtils.getTextContentWithoutDuplicatedSpaces(modelNoValueElement).replaceAll("[^0-9a-zA-Z\\-]", "").trim();
        CRAWLER.productInfo.setModelNo(modelNo);
        break;
      }
    }
  }

  void scrapeCategoryRanking(List<String> categoryInfoList) {

    if (categoryInfoList.size() <= 0) {
      LOGGER.info(String.format("Could not find category rankings for product %s:%s", CRAWLER.siteName, CRAWLER.productCode));
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

      CRAWLER.productInfo.addCategoryRanking(path, rank);
    }
  }

  // category ranking is from li#salesrank
  List<String> scrapeCategoryInfoListBySalesRank(String salesRankSelector, Closure<?> setProps) {
    LOGGER.info("[scrapeCategoryInfoListBySalesRank] in");
    List<String> categoryInfoList = new ArrayList<>();

    // properties for scraping (1st_line_regex, rest_ranks_selector, rest_paths_selector)
    Map<String, String> props = new HashMap<>();
    setProps.call(props);

    DomNode node = CRAWLER.productPage.querySelector(salesRankSelector);
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

  // category ranking is from product table
  List<String> scrapeCategoryInfoListByProductInfoTable(String productInfoTableSelector, Closure<?> setProps, Closure<Boolean> rankLineTest) {
    LOGGER.info("[scrapeCategoryInfoListByProductInfoTable] in");

    // properties for scraping (lines_selector, ranks_selector)
    Map<String, String> props = new HashMap<>();
    setProps.call(props);

    DomNode node = CRAWLER.productPage.querySelector(productInfoTableSelector);
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

  void save() {
    CRAWLER.savedPath = CRAWLER.webpageService.save("product", CRAWLER.siteName, CRAWLER.productPage.getWebResponse().getContentAsString());
  }

  // Wrapper
  String getTextContent(HtmlElement element) {
    return HtmlUtils.getTextContent(element);
  }

  void info(String str) {
    LOGGER.info(str);
  }
}
