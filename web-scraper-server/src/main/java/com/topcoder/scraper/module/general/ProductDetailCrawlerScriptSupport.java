package com.topcoder.scraper.module.general;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.util.HtmlUtils;
import com.topcoder.scraper.module.navpage.NavigableProductDetailPage;
import com.topcoder.scraper.service.WebpageService;

import groovy.lang.Closure;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//Make not abstract or static? Edit: Can't!
public abstract class ProductDetailCrawlerScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductDetailCrawlerScriptSupport.class);
  private static ProductDetailCrawler CRAWLER;
  private NavigableProductDetailPage detailPage;
  private WebpageService webpageService;
  private String siteName;
  private ProductInfo productInfo; // Not good?
  public static String productId = null;

  static void setCrawler(ProductDetailCrawler crawler) {
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
    detailPage.setEnableJS(value);
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

  void setCode(String selector) { 
    System.out.println("");
    System.out.println("Setting code!");
    System.out.println("");
    detailPage.setCode(selector);
  }

  void type(String input, String selector) { 
    detailPage.type(input, selector);
  }

  void setName(String selector) { 
    System.out.println("");
    System.out.println("Setting name!");
    System.out.println("");
    detailPage.setName(selector);
  }

  void setDistributor(String selector) { 
    System.out.println("");
    System.out.println("Setting distributor! ");
    System.out.println("");
    detailPage.setDistributor(selector);
  }

  void setPrice(String selector) { 
    System.out.println("");
    System.out.println("Setting price!");
    System.out.println("");
    detailPage.setPrice(selector);
  }

  void setQuantity(String str) {
    detailPage.setQuantity(str);
  }

  void setModelNo(String str) {
    detailPage.setModelNo(str);
  }

  void log(String str) { 
    System.out.println("___LOG___");
    System.out.println(str);
    System.out.println("_________");
  }

}
