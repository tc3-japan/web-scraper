package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.scraper.lib.navpage.NavigableProductDetailPage;

import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

//Make not abstract or static? Edit: Can't!
public abstract class GeneralProductDetailCrawlerScriptSupport extends Script {

  //private static final Logger LOGGER = LoggerFactory.getLogger(GeneralProductDetailCrawlerScriptSupport.class);
  private static GeneralProductDetailCrawler CRAWLER;
  private NavigableProductDetailPage detailPage;
  //private WebpageService webpageService;
  //private String siteName;
  private ProductInfo productInfo; // Not good?
  public static String productId = null;

  static void setCrawler(GeneralProductDetailCrawler crawler) {
    CRAWLER = crawler;
  }

  static void setProductId(String id) {
    productId = id;
  }

  public ProductInfo getProductInfo() {
    return productInfo;
  }

  void setPage(String str) { 
    productInfo = new ProductInfo(); // Not good?
    System.out.println("");
    System.out.println("Setting page to: " + str);
    System.out.println("CrawlerWC: " + CRAWLER.webClient);
    System.out.println("productInfo: " + productInfo);
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
      detailPage = new NavigableProductDetailPage(page, CRAWLER.webClient, productInfo);
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
    detailPage.savePage(name, siteName, CRAWLER.webpageService); //nullcheck?
  }

  void click(String selector) {
    detailPage.click(selector); //nullcheck?
    System.out.println("");
    System.out.println("Clicking: " + selector);
    System.out.println("");
  }

  void scrapeCode(String selector) { 
    System.out.println("");
    System.out.println("Setting code!");
    System.out.println("");
    detailPage.scrapeCode(selector);
  }

  void type(String input, String selector) { 
    detailPage.type(input, selector);
  }

  void scrapeName(String selector) { 
    System.out.println("");
    System.out.println("Setting name!");
    System.out.println("");
    detailPage.scrapeName(selector);
  }

  void scrapeDistributor(String selector) { 
    System.out.println("");
    System.out.println("Setting distributor! ");
    System.out.println("");
    detailPage.scrapeDistributor(selector);
  }

  void scrapePrice(String selector) { 
    System.out.println("");
    System.out.println("Setting price!");
    System.out.println("");
    detailPage.scrapePrice(selector);
  }

  void scrapePrices(List<String> selectors) {
    detailPage.scrapePrices(selectors);
  }

  void scrapeQuantity(String selector) {
    detailPage.scrapeQuantity(selector);
  }

  void scrapeModelNo(String selector) {
    detailPage.scrapeModelNo(selector);
  }

  void log(String str) { 
    System.out.println("___LOG___");
    System.out.println(str);
    System.out.println("_________");
  }

}
