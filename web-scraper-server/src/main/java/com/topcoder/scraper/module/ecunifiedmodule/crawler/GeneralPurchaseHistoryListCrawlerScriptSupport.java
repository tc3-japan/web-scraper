package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.scraper.lib.navpage.NavigableProductDetailPage;
import com.topcoder.scraper.lib.navpage.NavigablePurchaseHistoryPage;
import com.topcoder.scraper.service.WebpageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.Script;

//Make not abstract or static? Edit: Can't!
public abstract class GeneralPurchaseHistoryListCrawlerScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPurchaseHistoryListCrawlerScriptSupport.class);
  private static GeneralPurchaseHistoryListCrawler CRAWLER;
  private WebpageService webpageService;
  private String siteName;
  public static String productId = null;
  public NavigablePurchaseHistoryPage historyPage;


  static void setCrawler(GeneralPurchaseHistoryListCrawler crawler) {
    CRAWLER = crawler;
  }

  static void setProductId(String id) {
    productId = id;
  }


  void setPage(String str) { 
    System.out.println("");
    System.out.println("Setting page to: " + str);
    System.out.println("");
    HtmlPage page = null;
    try {
      System.out.println("JS Status: " + CRAWLER.webClient.getWebClient().getOptions().isJavaScriptEnabled());
      CRAWLER.webClient.getWebClient().getOptions().setJavaScriptEnabled(false); //TODO: TEST ONLY
      page = CRAWLER.webClient.getPage(str);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("");
    System.out.println("Page: " + page);
    System.out.println("");
    if(page != null) {
      historyPage = new NavigablePurchaseHistoryPage(page, CRAWLER.webClient);
    } else {
      System.out.println("Could not set page in ProductDetailScriptSupport.java@setPage()");
    }
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
    historyPage.click(selector); //nullcheck?
    System.out.println("");
    System.out.println("setting order number: " + selector);
    System.out.println("");
    historyPage.scrapeOrderNumber(orderNode, selector);
  }

  void setOrderDate(DomNode orderNode, String selector) {
    historyPage.click(selector); //nullcheck?
    System.out.println("");
    System.out.println("setting order date: " + selector);
    System.out.println("");
    historyPage.scrapeOrderDate(orderNode, selector);
  }

  void setOrderNumber(String selector) {
    historyPage.click(selector); //nullcheck?
    System.out.println("");
    System.out.println("setting order number: " + selector);
    System.out.println("");
    historyPage.scrapeOrderNumber(selector);
  }

  void setOrderDate(String selector) {
    historyPage.click(selector); //nullcheck?
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

}
