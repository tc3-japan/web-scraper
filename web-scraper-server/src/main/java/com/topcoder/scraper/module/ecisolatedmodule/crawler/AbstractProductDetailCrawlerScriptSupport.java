package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class AbstractProductDetailCrawlerScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProductDetailCrawlerScriptSupport.class);

  protected AbstractProductDetailCrawler crawler;

  void setCrawler(AbstractProductDetailCrawler crawler) {
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

  void save() {
    this.crawler.save();
  }

  void click(String selector) {
    this.crawler.getDetailPage().click(selector);
  }

  void type(String input, String selector) {
    this.crawler.getDetailPage().type(input, selector);
  }

  // Scraping wrapper: product in purchase history ---------------------------------------------------------------------
  void scrapeCode(String selector) {
    LOGGER.info("scrape product code");
    this.crawler.getDetailPage().scrapeCode(selector);
  }

  void scrapeName(String selector) {
    LOGGER.info("scrape product name");
    this.crawler.getDetailPage().scrapeName(selector);
  }

  void scrapeDistributor(String selector) {
    LOGGER.info("scrape product distributor");
    this.crawler.getDetailPage().scrapeDistributor(selector);
  }

  void scrapePrice(String selector) {
    LOGGER.info("scrape product price");
    this.crawler.getDetailPage().scrapePrice(selector);
  }

  void scrapePrices(List<String> selectors) {
    LOGGER.info("scrape product prices");
    this.crawler.getDetailPage().scrapePrices(selectors);
  }

  void scrapeQuantity(String selector) {
    LOGGER.info("scrape product quantity");
    this.crawler.getDetailPage().scrapeQuantity(selector);
  }

  void scrapeModelNo(String selector) {
    LOGGER.info("scrape product model no");
    this.crawler.getDetailPage().scrapeModelNo(selector);
  }

  void scrapeModelNo(List<Map<String, String>> modelNoSelectors) {
    LOGGER.info("scrape product model no");
    this.crawler.getDetailPage().scrapeModelNo(modelNoSelectors);
  }

  // Others: logging ---------------------------------------------------------------------------------------------------
  void log(String str) {
    LOGGER.info(str);
  }
}
