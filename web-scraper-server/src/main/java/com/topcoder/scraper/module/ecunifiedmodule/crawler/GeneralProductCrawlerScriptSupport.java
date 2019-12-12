package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import groovy.lang.Closure;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class GeneralProductCrawlerScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralProductCrawlerScriptSupport.class);

  protected GeneralProductCrawler crawler;

  void setCrawler(GeneralProductCrawler crawler) {
    this.crawler = crawler;
  }

  void setPage(String productUrl) {
    LOGGER.info("[setPage] in");

    this.crawler.getWebClient().getWebClient().getOptions().setJavaScriptEnabled(false); //TODO: TEST ONLY
    this.crawler.getDetailPage().setPage(productUrl);
  }

  void searchProducts(String searchUrlBase) {
    LOGGER.info("[searchProducts] in");

    this.crawler.getWebClient().getWebClient().getOptions().setJavaScriptEnabled(false);

    String searchUrl = searchUrlBase + this.crawler.getSearchWord();
    LOGGER.info("[searchProducts] Product URL: " + searchUrl);
    this.crawler.getListPage().setPage(searchUrl);
  }

  void searchProductsUsingForm(String searchUrl, String searchFormName, String searchInputName, String searchButtonSelector) {
    LOGGER.info("[searchProductsUsingForm] in");

    this.crawler.getWebClient().getWebClient().getOptions().setJavaScriptEnabled(false);
    this.crawler.getListPage().searchProductsUsingForm(searchUrl, searchFormName, searchInputName, searchButtonSelector, this.crawler.getSearchWord());
  }


  void setEnableJS(boolean value) {
    this.crawler.getWebClient().getWebClient().getOptions().setJavaScriptEnabled(value);
  }

  void savePage(String name) {
    this.crawler.getDetailPage().savePage(name, this.crawler.getSiteName(), this.crawler.getWebpageService());
  }

  void saveListPage(String name) {
    this.crawler.getListPage().savePage(name, this.crawler.getSiteName(), this.crawler.getWebpageService());
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

  void scrapeCodeFromAttr(String selector, String attrName, String codeRegexStr) {
    this.crawler.getDetailPage().scrapeCodeFromAttr(selector, attrName, codeRegexStr);
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

  String scrapeProductCodeFromSearchResultByProductAttrName(String searchResultSelector, String productCodeAttribute, String adProductClass) {
    String productCode = this.crawler.getListPage().scrapeProductCodeFromSearchResultByProductAttrName(
            this.crawler.getSearchWord(), searchResultSelector, productCodeAttribute, adProductClass, null);
    return productCode;
  }

  String scrapeProductCodeFromSearchResultByProductAttrName(String searchResultSelector, String productCodeAttribute, String adProductClass, String productCodeRegex) {
    String productCode = this.crawler.getListPage().scrapeProductCodeFromSearchResultByProductAttrName(
            this.crawler.getSearchWord(), searchResultSelector, productCodeAttribute, adProductClass, productCodeRegex);
    return productCode;
  }

  String scrapeProductCodeFromSearchResultByProductUrl(String searchResultSelector, String productCodeRegex) {
    String productCode = this.crawler.getListPage().scrapeProductCodeFromSearchResultByProductUrl(
            this.crawler.getSearchWord(), searchResultSelector, productCodeRegex);
    return productCode;
  }

  String eachProducts(Closure<String> closure) {
    return this.crawler.eachProducts(closure);
  }

  // Others: logging ---------------------------------------------------------------------------------------------------
  void log(String str) {
    LOGGER.info(str);
  }

}
