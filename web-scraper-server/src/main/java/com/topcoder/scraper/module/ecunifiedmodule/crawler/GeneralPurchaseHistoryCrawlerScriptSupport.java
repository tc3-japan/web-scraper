package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.util.Common;
import com.topcoder.scraper.lib.navpage.NavigableProductDetailPage;
import com.topcoder.scraper.lib.navpage.NavigablePurchaseHistoryPage;

import groovy.lang.Closure;
import groovy.lang.Script;

public abstract class GeneralPurchaseHistoryCrawlerScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPurchaseHistoryCrawlerScriptSupport.class);

  protected GeneralPurchaseHistoryCrawler crawler;
  void setCrawler(GeneralPurchaseHistoryCrawler crawler) {
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
    this.crawler.getHistoryPage().savePage(name, siteName, this.crawler.getWebpageService());
  }

  void click(String selector) {
    this.crawler.getHistoryPage().click(selector);
  }

  void openPage(DomNode node, String selector, Closure<Boolean> closure) {
    this.crawler.getHistoryPage().openPage(node, selector, closure, this.crawler.getWebpageService());
  }

  void click(DomNode node, String selector) {
    this.crawler.getHistoryPage().click(node, selector);
  }


  void type(String input, String selector) {
    this.crawler.getHistoryPage().type(input, selector);
  }

  URL getPageUrl() {
    return this.crawler.getHistoryPage().getPageUrl();
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

  PurchaseHistory getPurchaseHistory() {
    return this.crawler.getCurrentPurchaseHistory();
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

  public void addProduct(ProductInfo product) {
    this.crawler.getHistoryPage().addProduct(product);
  }

  public ProductInfo getProductInfo() {
    return this.crawler.getCurrentProduct();
  }

  String getText(String selector) {
    return this.crawler.getHistoryPage().getText(selector);
  }

  String getText(DomNode node, String selector) {
    return this.crawler.getHistoryPage().getText(node, selector);
  }

  String getNodeAttribute(String selector, String attr) {
    return this.crawler.getHistoryPage().getNodeAttribute(selector, attr);
  }

  String getNodeAttribute(DomNode node, String selector, String attr) {
    return this.crawler.getHistoryPage().getNodeAttribute(node, selector, attr);
  }

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

  String scrapeText(String... selectors) {
    NavigablePurchaseHistoryPage page = this.crawler.getHistoryPage();
    if (page == null) {
      return null;
    }
    return page.getText(selectors);
  }

  String normalize(String code) {
    return Common.normalize(code);
  }

  // Others: logging ---------------------------------------------------------------------------------------------------
  void log(String str) {
    LOGGER.info(str);
  }

  void debug(String str) {
    LOGGER.debug(str);
  }
}
