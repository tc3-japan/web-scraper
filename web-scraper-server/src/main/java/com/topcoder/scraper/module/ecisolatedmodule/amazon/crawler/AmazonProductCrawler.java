package com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.topcoder.common.util.HtmlUtils;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractProductCrawler;
import com.topcoder.scraper.service.WebpageService;
import groovy.lang.Closure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Amazon implementation of ProductDetailCrawler
 */
@Component
public class AmazonProductCrawler extends AbstractProductCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonProductCrawler.class);

  @Autowired
  public AmazonProductCrawler(WebpageService webpageService) {
    super("amazon", webpageService);
  }

  @Override
  public String getScriptSupportClassName() {
    return AmazonProductCrawlerScriptSupport.class.getName();
  }

  public void scrapeCategoryRanking(List<String> categoryInfoList) {
    LOGGER.debug("[scrapeCategoryRanking] in");

    if (categoryInfoList.size() <= 0) {
      LOGGER.info(String.format("Could not find category rankings for product %s:%s", this.siteName, this.productCode));
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

  // category ranking is from li#salesrank
  public List<String> scrapeCategoryInfoListBySalesRank(String salesRankSelector, Closure<?> setProps) {
    LOGGER.debug("[scrapeCategoryInfoListBySalesRank] in");
    List<String> categoryInfoList = new ArrayList<>();

    // properties for scraping (1st_line_regex, rest_ranks_selector, rest_paths_selector)
    Map<String, String> props = new HashMap<>();
    setProps.call(props);

    DomNode node = this.detailPage.getPage().querySelector(salesRankSelector);
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
  public List<String> scrapeCategoryInfoListByProductInfoTable(String productInfoTableSelector, Closure<?> setProps, Closure<Boolean> rankLineTest) {
    LOGGER.debug("[scrapeCategoryInfoListByProductInfoTable] in");

    // properties for scraping (lines_selector, ranks_selector)
    Map<String, String> props = new HashMap<>();
    setProps.call(props);

    DomNode node = this.detailPage.getPage().querySelector(productInfoTableSelector);
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
}
