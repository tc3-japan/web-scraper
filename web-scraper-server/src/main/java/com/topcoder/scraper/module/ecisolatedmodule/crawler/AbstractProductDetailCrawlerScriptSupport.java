package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.scraper.lib.navpage.NavigableProductDetailPage;
import com.topcoder.scraper.service.WebpageService;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public abstract class AbstractProductDetailCrawlerScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProductDetailCrawlerScriptSupport.class);
  protected static AbstractProductDetailCrawler CRAWLER;
  protected static String productId = null;

  protected NavigableProductDetailPage detailPage;
  protected WebpageService webpageService;
  protected String siteName;
  protected ProductInfo productInfo; // Not good?

  static void setCrawler(AbstractProductDetailCrawler crawler) {
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
    LOGGER.info("Setting page to: " + str);
    LOGGER.info("CrawlerWC: " + CRAWLER.webClient);
    LOGGER.info("productInfo: " + productInfo);
    HtmlPage page = null;
    try {
      LOGGER.info("JS Status: " + CRAWLER.webClient.getWebClient().getOptions().isJavaScriptEnabled());
      CRAWLER.webClient.getWebClient().getOptions().setJavaScriptEnabled(false); //TODO: TEST ONLY
      page = CRAWLER.webClient.getPage(str);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    LOGGER.info("Page: " + page);
    if(page != null) {
      detailPage = new NavigableProductDetailPage(page, CRAWLER.webClient, productInfo);
    } else {
      LOGGER.info("Could not set page in ProductDetailScriptSupport.java@setPage()");
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
    LOGGER.info("Clicking: " + selector);
  }

  void scrapeCode(String selector) { 
    LOGGER.info("Setting code!");
    detailPage.scrapeCode(selector);
  }

  void type(String input, String selector) { 
    detailPage.type(input, selector);
  }

  void scrapeName(String selector) { 
    LOGGER.info("Setting name!");
    detailPage.scrapeName(selector);
  }

  void scrapeDistributor(String selector) { 
    LOGGER.info("Setting distributor! ");
    detailPage.scrapeDistributor(selector);
  }

  void scrapePrice(String selector) { 
    LOGGER.info("Setting price!");
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
    LOGGER.info(str);
  }

}
