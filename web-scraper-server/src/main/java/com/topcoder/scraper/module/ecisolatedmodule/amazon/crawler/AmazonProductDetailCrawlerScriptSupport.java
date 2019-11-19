package com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler;

import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractProductDetailCrawlerScriptSupport;
import groovy.lang.Closure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Amazon implementation of ProductDetailCrawlerScriptSupport
 */
public abstract class AmazonProductDetailCrawlerScriptSupport extends AbstractProductDetailCrawlerScriptSupport {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonProductDetailCrawlerScriptSupport.class);

  void scrapeCategoryRanking(List<String> categoryInfoList) {
    ((AmazonProductDetailCrawler)this.crawler).scrapeCategoryRanking(categoryInfoList);
  }

  List<String> scrapeCategoryInfoListBySalesRank(String salesRankSelector, Closure<?> setProps) {
    return ((AmazonProductDetailCrawler)this.crawler).scrapeCategoryInfoListBySalesRank(salesRankSelector, setProps);
  }

  List<String> scrapeCategoryInfoListByProductInfoTable(String productInfoTableSelector, Closure<?> setProps, Closure<Boolean> rankLineTest) {
    return ((AmazonProductDetailCrawler)this.crawler).scrapeCategoryInfoListByProductInfoTable(productInfoTableSelector, setProps, rankLineTest);
  }
}
