package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.topcoder.scraper.lib.navpage.NavigablePurchaseHistoryPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.Closure;
import groovy.lang.Script;

//Make not abstract or static? Edit: Can't!
public abstract class GeneralPurchaseHistoryListCrawlerScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPurchaseHistoryListCrawlerScriptSupport.class);
  private static GeneralPurchaseHistoryListCrawler CRAWLER;
  //private WebpageService webpageService;
  //private String siteName;
  public static String productId = null;
  public NavigablePurchaseHistoryPage historyPage;


  static void setCrawler(GeneralPurchaseHistoryListCrawler crawler) {
    CRAWLER = crawler;
  }

  static void setProductId(String id) {
    productId = id;
  }

  void setPage(String historyUrl) {
    CRAWLER.webClient.getWebClient().getOptions().setJavaScriptEnabled(false); //TODO: TEST ONLY
    CRAWLER.getHistoryPage().setPage(historyUrl);
    historyPage = CRAWLER.getHistoryPage();
  }

  void setEnableJS(boolean value) {
    // TODO : reconsider relationship between webClient and page
    CRAWLER.webClient.getWebClient().getOptions().setJavaScriptEnabled(value);
    //detailPage.setEnableJS(value);
  }

  void savePage(String name, String siteName) {
    historyPage.savePage(name, siteName, CRAWLER.webpageService); //nullcheck?
  }

  void click(String selector) {
    historyPage.click(selector); //nullcheck?
    System.out.println("");
    System.out.println("Clicking: " + selector);
    System.out.println("");
  }

  void type(String input, String selector) { 
    historyPage.type(input, selector);
  }

  void setOrderNumber(DomNode orderNode, String selector) {
    //historyPage.click(selector); //nullcheck? DON'T CLICK
    System.out.println("");
    System.out.println("setting order number: " + selector);
    System.out.println("");
    historyPage.scrapeOrderNumber(orderNode, selector);
  }

  //TODO
  void setOrderNumberGeneral(DomNode orderNode, String selector) {
    //historyPage.clickElement(selector); //nullcheck?
    System.out.println("");
    System.out.println("setting order number: " + selector);
    System.out.println("");
    historyPage.scrapeOrderNumber(orderNode, selector);
  }

  void setOrderDate(DomNode orderNode, String selector) {
    //historyPage.click(selector); //nullcheck?  DON'T CLICK
    System.out.println("");
    System.out.println("setting order date: " + selector);
    System.out.println("");
    historyPage.scrapeOrderDate(orderNode, selector);
  }

  void setOrderNumber(String selector) {
    //historyPage.click(selector); //nullcheck?  DON'T CLICK
    System.out.println("");
    System.out.println("setting order number: " + selector);
    System.out.println("");
    historyPage.scrapeOrderNumber(selector);
  }

  void setOrderDate(String selector) {
    //historyPage.click(selector); //nullcheck?  DON'T CLICK
    System.out.println("");
    System.out.println("setting order date: " + selector);
    System.out.println("");
    historyPage.scrapeOrderDate(selector);
  }

  void log(String str) { 
    System.out.println("___LOG___");
    System.out.println(str);
    System.out.println("_________");
  }

  void processPurchaseHistory(Closure<Boolean> closure) throws IOException {
    System.out.println();
    System.out.println("Closure: " + closure);
    System.out.println("CRAWLER: " + CRAWLER);

    
    CRAWLER.processPurchaseHistory(closure);
  }

  void processOrders(List<DomNode> orderList , Closure<Boolean> closure) {
    CRAWLER.processOrders(orderList, closure);
  }

  void processProducts(List<DomNode> productList , Closure<Boolean> closure) {
    CRAWLER.processProducts(productList, closure);
  }

  // Crawler method: others --------------------------------------------------------------------------------------------

  boolean isNew() {
    return CRAWLER.isNew();
  }

  // Scraping wrapper: general -----------------------------------------------------------------------------------------

  List<DomNode> scrapeDomList(String selector) {
    LOGGER.info("scrape dom list: " + selector);
    return historyPage.scrapeDomList(selector);
  }

  // Scraping wrapper: order in purchase history -----------------------------------------------------------------------

  void scrapeOrderNumber(DomNode orderNode, String selector) {
    LOGGER.info("scrape order number");
    historyPage.scrapeOrderNumber(orderNode, selector);
  }

  void scrapeOrderNumberWithRegex(DomNode orderNode, String selector, String regexStr) {
    LOGGER.info("scrape order number");
    historyPage.scrapeOrderNumberWithRegex(orderNode, selector, regexStr);
  }

  void scrapeOrderDate(DomNode orderNode, String selector) {
    LOGGER.info("scrape order date");
    historyPage.scrapeOrderDate(orderNode, selector);
  }

  void scrapeOrderDateDefault(DomNode orderNode, String selector) {
    LOGGER.info("scrape order date");
    historyPage.scrapeOrderDateDefault(orderNode, selector);
  }

  void scrapeTotalAmount(DomNode orderNode, String selector) {
    LOGGER.info("scrape order date");
    historyPage.scrapeTotalAmount(orderNode, selector);
  }

  void scrapeDeliveryStatus(DomNode orderNode, String selector) {
    LOGGER.info("scrape delivery status");
    historyPage.scrapeDeliveryStatus(orderNode, selector);
  }

  // Scraping wrapper: product in order --------------------------------------------------------------------------------

  void scrapeProductCodeFromAnchor(DomNode productNode, String selector, String regexStr) {
    LOGGER.info("scrape product code");
    historyPage.scrapeProductCodeFromAnchor(productNode, selector, regexStr);
  }

  void scrapeProductName(DomNode productNode, String selector) {
    LOGGER.info("scrape product name");
    historyPage.scrapeProductName(productNode, selector);
  }

  void scrapeProductNameFromAnchor(DomNode productNode, String selector) {
    LOGGER.info("scrape product name");
    historyPage.scrapeProductNameFromAnchor(productNode, selector);
  }

  void scrapeUnitPrice(DomNode productNode, String selector) {
    LOGGER.info("scrape unit price");
    historyPage.scrapeUnitPrice(productNode, selector);
  }

  void scrapeProductQuantity(DomNode productNode, String selector) {
    LOGGER.info("scrape product quantity");
    historyPage.scrapeProductQuantity(productNode, selector);
  }

  void scrapeProductDistributor(DomNode productNode, String selector) {
    LOGGER.info("scrape product distributor");
    historyPage.scrapeProductDistributor(productNode, selector);
  }

}
