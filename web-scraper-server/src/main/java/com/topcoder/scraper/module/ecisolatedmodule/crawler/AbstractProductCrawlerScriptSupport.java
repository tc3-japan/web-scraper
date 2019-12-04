package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class AbstractProductCrawlerScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProductCrawlerScriptSupport.class);

  protected AbstractProductCrawler crawler;

  void setCrawler(AbstractProductCrawler crawler) {
    this.crawler = crawler;
  }

  void setPage(String productUrl) {
    this.crawler.getWebClient().getWebClient().getOptions().setJavaScriptEnabled(false); //TODO: TEST ONLY
    this.crawler.getDetailPage().setPage(productUrl);
  }

  void setEnableJS(boolean value) {
    this.crawler.getWebClient().getWebClient().getOptions().setJavaScriptEnabled(value);
  }

  void savePage(String name) {
    this.crawler.getDetailPage().savePage(name, this.crawler.getSiteName(), this.crawler.getWebpageService());
  }

  void click(String selector) {
    this.crawler.getDetailPage().click(selector);
  }

  void type(String input, String selector) {
    this.crawler.getDetailPage().type(input, selector);
  }

  // Scraping wrapper: product -----------------------------------------------------------------------------------------
  void scrapeCode(String selector) {
    this.crawler.getDetailPage().scrapeCode(selector);
  }

  void scrapeName(String selector) {
    this.crawler.getDetailPage().scrapeName(selector);
  }

  void scrapeDistributor(String selector) {
    this.crawler.getDetailPage().scrapeDistributor(selector);
  }

  void scrapePrice(String selector) {
    this.crawler.getDetailPage().scrapePrice(selector);
  }

  void scrapePrices(List<String> selectors) {
    this.crawler.getDetailPage().scrapePrices(selectors);
  }

  void scrapeQuantity(String selector) {
    this.crawler.getDetailPage().scrapeQuantity(selector);
  }

  void scrapeModelNo(String selector) {
    this.crawler.getDetailPage().scrapeModelNo(selector);
  }

  void scrapeModelNo(List<Map<String, String>> modelNoSelectors) {
    this.crawler.getDetailPage().scrapeModelNo(modelNoSelectors);
  }

  // Others: logging ---------------------------------------------------------------------------------------------------
  void log(String str) {
    LOGGER.info(str);
  }
}
