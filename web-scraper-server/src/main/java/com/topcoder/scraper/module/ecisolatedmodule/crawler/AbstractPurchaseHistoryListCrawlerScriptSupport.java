package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import com.gargoylesoftware.htmlunit.html.DomNode;
import groovy.lang.Closure;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public abstract class AbstractPurchaseHistoryListCrawlerScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPurchaseHistoryListCrawlerScriptSupport.class);

  protected AbstractPurchaseHistoryListCrawler crawler;
  void setCrawler(AbstractPurchaseHistoryListCrawler crawler) {
    this.crawler = crawler;
  }

  void setPage(String historyUrl) {
    this.crawler.getWebClient().getWebClient().getOptions().setJavaScriptEnabled(false); //TODO: TEST ONLY
    this.crawler.getHistoryPage().setPage(historyUrl);
  }

  void setEnableJS(boolean value) {
    this.crawler.getWebClient().getWebClient().getOptions().setJavaScriptEnabled(value);
  }

  void savePage(String name, String siteName) {
    this.crawler.getHistoryPage().savePage(name, siteName, this.crawler.getWebpageService()); //nullcheck?
  }

  void click(String selector) {
    this.crawler.getHistoryPage().click(selector); //nullcheck?
  }

  void type(String input, String selector) {
    this.crawler.getHistoryPage().type(input, selector);
  }

  // Crawler method: purchase history structure processor --------------------------------------------------------------

  void processPurchaseHistory(Closure<Boolean> closure) throws IOException {
    this.crawler.processPurchaseHistory(closure);
  }

  void processOrders(List<DomNode> orderList , Closure<Boolean> closure) {
    this.crawler.processOrders(orderList, closure);
  }

  void processProducts(List<DomNode> productList , Closure<Boolean> closure) {
    this.crawler.processProducts(productList, closure);
  }

  // Crawler method: others --------------------------------------------------------------------------------------------

  boolean isNew() {
    return this.crawler.isNew();
  }

  // Scraping wrapper: general -----------------------------------------------------------------------------------------

  List<DomNode> scrapeDomList(String selector) {
    LOGGER.info("scrape dom list: " + selector);
    return this.crawler.getHistoryPage().scrapeDomList(selector);
  }

  // Scraping wrapper: order in purchase history -----------------------------------------------------------------------

  void scrapeOrderNumber(DomNode orderNode, String selector) {
    LOGGER.info("scrape order number");
    this.crawler.getHistoryPage().scrapeOrderNumber(orderNode, selector);
  }

  void scrapeOrderNumberWithRegex(DomNode orderNode, String selector, String regexStr) {
    LOGGER.info("scrape order number");
    this.crawler.getHistoryPage().scrapeOrderNumberWithRegex(orderNode, selector, regexStr);
  }

  void scrapeOrderDate(DomNode orderNode, String selector) {
    LOGGER.info("scrape order date");
    this.crawler.getHistoryPage().scrapeOrderDate(orderNode, selector);
  }

  void scrapeOrderDateDefault(DomNode orderNode, String selector) {
    LOGGER.info("scrape order date");
    this.crawler.getHistoryPage().scrapeOrderDateDefault(orderNode, selector);
  }

  void scrapeTotalAmount(DomNode orderNode, String selector) {
    LOGGER.info("scrape order date");
    this.crawler.getHistoryPage().scrapeTotalAmount(orderNode, selector);
  }

  void scrapeDeliveryStatus(DomNode orderNode, String selector) {
    LOGGER.info("scrape delivery status");
    this.crawler.getHistoryPage().scrapeDeliveryStatus(orderNode, selector);
  }

  // Scraping wrapper: product in order --------------------------------------------------------------------------------

  void scrapeProductCodeFromAnchor(DomNode productNode, String selector, String regexStr) {
    LOGGER.info("scrape product code");
    this.crawler.getHistoryPage().scrapeProductCodeFromAnchor(productNode, selector, regexStr);
  }

  void scrapeProductName(DomNode productNode, String selector) {
    LOGGER.info("scrape product name");
    this.crawler.getHistoryPage().scrapeProductName(productNode, selector);
  }

  void scrapeProductNameFromAnchor(DomNode productNode, String selector) {
    LOGGER.info("scrape product name");
    this.crawler.getHistoryPage().scrapeProductNameFromAnchor(productNode, selector);
  }

  void scrapeUnitPrice(DomNode productNode, String selector) {
    LOGGER.info("scrape unit price");
    this.crawler.getHistoryPage().scrapeUnitPrice(productNode, selector);
  }

  void scrapeProductQuantity(DomNode productNode, String selector) {
    LOGGER.info("scrape product quantity");
    this.crawler.getHistoryPage().scrapeProductQuantity(productNode, selector);
  }

  void scrapeProductDistributor(DomNode productNode, String selector) {
    LOGGER.info("scrape product distributor");
    this.crawler.getHistoryPage().scrapeProductDistributor(productNode, selector);
  }

  // Others: logging ---------------------------------------------------------------------------------------------------
  void log(String str) {
    LOGGER.info(str);
  }
}
