package com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler;

import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractProductCrawlerScriptSupport;
import groovy.lang.Closure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Amazon implementation of ProductCrawlerScriptSupport
 */
public abstract class AmazonProductCrawlerScriptSupport extends AbstractProductCrawlerScriptSupport {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonProductCrawlerScriptSupport.class);

  void scrapeCategoryRanking(List<String> categoryInfoList) {
    ((AmazonProductCrawler)this.crawler).scrapeCategoryRanking(categoryInfoList);
  }

  List<String> scrapeCategoryInfoListBySalesRank(String salesRankSelector, Closure<?> setProps) {
    return ((AmazonProductCrawler)this.crawler).scrapeCategoryInfoListBySalesRank(salesRankSelector, setProps);
  }

  List<String> scrapeCategoryInfoListByProductInfoTable(String productInfoTableSelector, Closure<?> setProps, Closure<Boolean> rankLineTest) {
    return ((AmazonProductCrawler)this.crawler).scrapeCategoryInfoListByProductInfoTable(productInfoTableSelector, setProps, rankLineTest);
  }
}
