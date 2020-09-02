package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import com.gargoylesoftware.htmlunit.html.DomNode;
import groovy.lang.Closure;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public abstract class AbstractPurchaseHistoryCrawlerScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPurchaseHistoryCrawlerScriptSupport.class);

  protected AbstractPurchaseHistoryCrawler crawler;
  void setCrawler(AbstractPurchaseHistoryCrawler crawler) {
    this.crawler = crawler;
  }

  void setPage(String historyUrl) {
    this.crawler.getWebClient().getWebClient().getOptions().setJavaScriptEnabled(false); //TODO: TEST ONLY
    this.crawler.getHistoryPage().setPage(historyUrl);
  }

  void setEnableJS(boolean value) {
    this.crawler.getWebClient().getWebClient().getOptions().setJavaScriptEnabled(value);
  }

  void savePage(String name) {
    this.crawler.getHistoryPage().savePage(name, this.crawler.getSiteName(), this.crawler.getWebpageService());
  }

  void click(String selector) {
    this.crawler.getHistoryPage().click(selector);
  }

  void openPage(DomNode node, String selector, Closure<Boolean> closure) {
    this.crawler.getHistoryPage().openPage(node, selector, closure, this.crawler.getWebpageService());
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
    return this.crawler.getHistoryPage().scrapeDomList(selector);
  }

  List<DomNode> scrapeDomList(DomNode node, String selector) {
    return this.crawler.getHistoryPage().scrapeDomList(node, selector);
  }

  // Scraping wrapper: order in purchase history -----------------------------------------------------------------------

  void scrapeOrderNumber(DomNode orderNode, String selector) {
    this.crawler.getHistoryPage().scrapeOrderNumber(orderNode, selector);
  }

  void scrapeOrderNumberWithRegex(DomNode orderNode, String selector, String regexStr) {
    this.crawler.getHistoryPage().scrapeOrderNumberWithRegex(orderNode, selector, regexStr);
  }

  void scrapeOrderDate(DomNode orderNode, String selector) {
    this.crawler.getHistoryPage().scrapeOrderDate(orderNode, selector);
  }

  void scrapeOrderDateDefault(DomNode orderNode, String selector) {
    this.crawler.getHistoryPage().scrapeOrderDateDefault(orderNode, selector);
  }

  void scrapeTotalAmount(DomNode orderNode, String selector) {
    this.crawler.getHistoryPage().scrapeTotalAmount(orderNode, selector);
  }

  void scrapeDeliveryStatus(DomNode orderNode, String selector) {
    this.crawler.getHistoryPage().scrapeDeliveryStatus(orderNode, selector);
  }

  // Scraping wrapper: product in order --------------------------------------------------------------------------------

  void scrapeProductCodeFromAnchor(DomNode productNode, String selector, String regexStr) {
    this.crawler.getHistoryPage().scrapeProductCodeFromAnchor(productNode, selector, regexStr);
  }

  void scrapeProductCodeFromInput(DomNode productNode, String selector, String regexStr) {
    this.crawler.getHistoryPage().scrapeProductCodeFromInput(productNode, selector, regexStr);
  }

  void scrapeProductCode(String selector) {
    this.crawler.getHistoryPage().scrapeProductCode(selector);
  }

  void scrapeProductName(DomNode productNode, String selector) {
    this.crawler.getHistoryPage().scrapeProductName(productNode, selector);
  }

  void scrapeProductNameFromAnchor(DomNode productNode, String selector) {
    this.crawler.getHistoryPage().scrapeProductNameFromAnchor(productNode, selector);
  }

  void scrapeUnitPrice(DomNode productNode, String selector) {
    this.crawler.getHistoryPage().scrapeUnitPrice(productNode, selector);
  }

  void scrapeProductQuantity(DomNode productNode, String selector) {
    this.crawler.getHistoryPage().scrapeProductQuantity(productNode, selector);
  }

  void scrapeProductDistributor(DomNode productNode, String selector) {
    this.crawler.getHistoryPage().scrapeProductDistributor(productNode, selector);
  }

  // Others: logging ---------------------------------------------------------------------------------------------------
  void log(String str) {
    LOGGER.info(str);
  }
}
