package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.util.Common;
import com.topcoder.scraper.lib.navpage.NavigablePage;
import com.topcoder.scraper.lib.navpage.NavigableProductDetailPage;
import groovy.lang.Closure;
import groovy.lang.Script;

import java.net.URL;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class GeneralProductCrawlerScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralProductCrawlerScriptSupport.class);

  protected GeneralProductCrawler crawler;

  void setCrawler(GeneralProductCrawler crawler) {
    this.crawler = crawler;
  }

  void setPage(String productUrl) {
    LOGGER.debug("[setPage] in");

    this.crawler.getWebClient().getWebClient().getOptions().setJavaScriptEnabled(false); //TODO: TEST ONLY
    this.crawler.getDetailPage().setPage(productUrl);
  }

  void searchProducts(String searchUrlBase) {
    LOGGER.debug("[searchProducts] in");

    this.crawler.getWebClient().getWebClient().getOptions().setJavaScriptEnabled(false);

    String searchUrl = searchUrlBase + this.crawler.getSearchWord();
    LOGGER.debug("[searchProducts] Product URL: " + searchUrl);
    this.crawler.getListPage().setPage(searchUrl);
  }

  void searchProductsUsingForm(String searchUrl, String searchFormName, String searchInputName, String searchButtonSelector) {
    LOGGER.debug("[searchProductsUsingForm] in");

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

  URL getPageUrl() {
    return this.crawler.getDetailPage().getPageUrl();
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

  void scrapeCategoryRanking(List<String> categoryInfoList) {
    this.crawler.getDetailPage().scrapeCategoryRanking(categoryInfoList);
  }

  List<String> scrapeCategoryInfoListBySalesRank(String salesRankSelector, Closure<?> setProps) {
    return this.crawler.getDetailPage().scrapeCategoryInfoListBySalesRank(salesRankSelector, setProps);
  }

  List<String> scrapeCategoryInfoListByProductInfoTable(String productInfoTableSelector, Closure<?> setProps, Closure<Boolean> rankLineTest) {
    return this.crawler.getDetailPage().scrapeCategoryInfoListByProductInfoTable(productInfoTableSelector, setProps, rankLineTest);
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

  ProductInfo getProductInfo() {
    return this.crawler.getProductInfo();
  }

  // Others: utility ---------------------------------------------------------------------------------------------------
  String scrapeText(String selector) {
    return getPage().getText(selector);
  }

  String scrapeText(String... selectors) {
    if (selectors == null || selectors.length == 0) {
      return null;
    }
    NavigablePage page = getPage();
    if (page == null) {
      return null;
    }
    return page.getText(selectors);
  }

  String getNodeAttribute(String selector, String attr) {
    return getPage().getNodeAttribute(selector, attr);
  }

  String getNodeAttribute(DomNode sourceNode, String selector, String attr) {
    return getPage().getNodeAttribute(sourceNode, selector, attr);
  }

  String normalize(String code) {
    return Common.normalize(code);
  }

  NavigablePage getPage() {
    if (this.crawler.getDetailPage() != null) {
      return this.crawler.getDetailPage();
    }
    return this.crawler.getListPage();
  }

  void log(String str) {
    LOGGER.info(str);
  }

  void debug(String str) {
    LOGGER.debug(str);
  }
}
