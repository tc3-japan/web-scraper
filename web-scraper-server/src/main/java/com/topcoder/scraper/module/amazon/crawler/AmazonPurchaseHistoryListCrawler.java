package com.topcoder.scraper.module.amazon.crawler;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.exception.SessionExpiredException;
import com.topcoder.scraper.service.WebpageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.topcoder.common.util.DateUtils.fromString;
import static com.topcoder.common.util.HtmlUtils.*;

/**
 * Crawl amazon purchase history page
 */
public class AmazonPurchaseHistoryListCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonPurchaseHistoryListCrawler.class);
  private final String siteName;
  private final AmazonProperty property;
  private final WebpageService webpageService;

  // TODO : delete, this is for PoC Code that limits the count to go to next page to 3
  private int nextPageCount;

  public AmazonPurchaseHistoryListCrawler(String siteName, AmazonProperty property, WebpageService webpageService) {
    this.siteName = siteName;
    this.property = property;
    this.webpageService = webpageService;
    this.nextPageCount = 0;
  }

  /**
   * Fetch purchase history
   * @param webClient the web client
   * @param lastPurchaseHistory optional, if it's empty, all orders will be fetched
   * @param saveHtml true if product html page will be saved
   * @return AmazonPurchaseHistoryListCrawlerResult
   * @throws IOException
   */
  public AmazonPurchaseHistoryListCrawlerResult fetchPurchaseHistoryList(TrafficWebClient webClient, PurchaseHistory lastPurchaseHistory, boolean saveHtml) throws IOException {
    List<PurchaseHistory> list = new LinkedList<>();
    List<String> pathList = new LinkedList<>();
    LOGGER.info("goto Order Page");

    HtmlPage page = webClient.getPage(property.getHistoryUrl());

    if (AmazonPurchaseHistoryListCrawler.isSessionExpired(page)) {
      throw new SessionExpiredException("Session has been expired.");
    }
    webpageService.save("order-home", siteName, page.getWebResponse().getContentAsString());    
    while (true) {
      if (page == null || !parsePurchaseHistory(list, page, lastPurchaseHistory, saveHtml, pathList)) {
        break;
      } else {
        page = gotoNextPage(page, webClient);
      }
    }

    return new AmazonPurchaseHistoryListCrawlerResult(list, pathList);
  }

  /**
   * check if next page button exist, or next time range is available
   *
   * @return next page if has next page
   */
  private HtmlPage gotoNextPage(HtmlPage page, TrafficWebClient webClient) throws IOException {

    HtmlSelect select = page.querySelector(property.getCrawling().getPurchaseHistoryListPage().getOrderFilter());
    // XPath Version query
    //String xxx      = page.getFirstByXPath(property.getCrawling().getPurchaseHistoryListPage().getXXX());

    String optionValue = "";
    String optionLabel = "";
    if (select != null) {
      if (select.getSelectedIndex() + 1 < select.getOptionSize()) {
        optionValue = select.getOption(select.getSelectedIndex() + 1).getValueAttribute();
      }
    }

    HtmlPage nextPage = null;
    // Try to click next page first
    HtmlAnchor nextPageAnchor = page.querySelector(property.getCrawling().getPurchaseHistoryListPage().getNextPage());
    if (nextPageAnchor != null) {
      // TODO : delete below condition, this is for PoC Code that limits the count to go to next page to 3
      if (nextPageCount >= 4) {
        LOGGER.info(">>> next page count limit exceeded.");

      } else {
        LOGGER.info(">>> next page count = " + nextPageCount);
        nextPageCount++;

        LOGGER.info("goto Next Page");

        // "click" doesn't work
        //nextPage = webClient.click(nextPageAnchor);
        // below code is work-around: use "getPage" instead of "click"
        String link = property.getUrl() + nextPageAnchor.getHrefAttribute();
        nextPage = webClient.getPage(property.getUrl()+nextPageAnchor.getHrefAttribute());
        webpageService.save("purchase-history_" + optionValue, siteName, nextPage.getWebResponse().getContentAsString());
        return nextPage;
      }
    }
    // TODO : delete, this is for PoC Code that limits the count to go to next page to 3
    nextPageCount = 0;

    // if pagination reaches end, try to go next time period
    if (select != null) {
      if (select.getSelectedIndex() + 1 < select.getOptionSize()) {
        optionValue = select.getOption(select.getSelectedIndex() + 1).getValueAttribute();
        optionLabel = select.getOption(select.getSelectedIndex() + 1).getText();
        // TODO: delete, this is for PoC Code that limits the purchase history to the one after 2017
        if (optionValue.startsWith("2017") || optionLabel.startsWith("2017")) {
          LOGGER.info("in dev: at 2017, return null and quit");
          return null;
        }
        LOGGER.info("goto " + optionLabel + ":" + optionValue + " Order Page");

        nextPage = webClient.getPage(property.getHistoryUrl() + optionValue);
        webpageService.save("purchase-history_" + optionValue, siteName, nextPage.getWebResponse().getContentAsString());
        return nextPage;
      }
    }

    return null;
  }

  /**
   * Parse purchase history from webpage
   *
   * @param list purchase history list
   * @param page html page
   * @param last last purchase history
   * @return true if all orders are new, requires checking next page
   */
  private boolean parsePurchaseHistory(
    List<PurchaseHistory> list, HtmlPage page,
    PurchaseHistory last, boolean saveHtml, List<String> pathList) {

    LOGGER.debug("Parsing page url " + page.getUrl().toString());

    List<DomNode> orders = page.querySelectorAll(property.getCrawling().getPurchaseHistoryListPage().getOrdersBox());
    // XPath Version query
    //List<DomNode> xxx  = page.getByXPath(property.getCrawling().getPurchaseHistoryListPage().getXXX());

    boolean hasNewOrder = orders.stream().allMatch(order -> parseOrder(list, order, last));

    // only save purchase history page is there is new order
    if (hasNewOrder && orders.size() > 0 && saveHtml) {
      String savedPath = webpageService.save("purchase-history", siteName, page.getWebResponse().getContentAsString());
      pathList.add(savedPath);
    }

    return hasNewOrder;
  }

  /**
   * Parse purchase history from an order element
   *
   * The web-scraper assumes the order that orderDate is greater than or equals to last one as `new order`.
   * If the order is `new order`, fetch process can continue.
   *
   * As other important process than above judgement, this method add the order to list if
   * - the orderNumber doesn't equals to last one.
   * - the order hasn't pushed in purchase history list yet.
   *
   * @param list purchase history list
   * @param order DomNode for one order
   * @param last last purchase history
   * @return true if all orders are new, requires checking next page
   */
  private boolean parseOrder(List<PurchaseHistory> list, DomNode order, PurchaseHistory last) {

    String date           = getTextContent(order.querySelector(property.getCrawling().getPurchaseHistoryListPage().getOrderDate()));
    String total          = getTextContent(order.querySelector(property.getCrawling().getPurchaseHistoryListPage().getTotalAmount()));
    String orderNumber    = getTextContent(order.querySelector(property.getCrawling().getPurchaseHistoryListPage().getOrderNumber()));
    String deliveryStatus = getTextContent(order.querySelector(property.getCrawling().getPurchaseHistoryListPage().getDeliveryStatus()));
    List<DomNode> products = order.querySelectorAll(property.getCrawling().getPurchaseHistoryListPage().getProductsBox());
    // XPath Version query
    //String xxx          = order.getFirstByXPath(property.getCrawling().getPurchaseHistoryListPage().getXXX());
    //List<DomNode> xxx   = order.getByXPath(property.getCrawling().getPurchaseHistoryListPage().getXXX());

    List<ProductInfo> productInfoList = products.stream().map(this::parseProduct).collect(Collectors.toList());

    Date orderDate = null;
    try {
      orderDate = fromString(date);
    } catch (ParseException e) {
    }

    PurchaseHistory ph = new PurchaseHistory(property.getUsername(), orderNumber, orderDate, total, productInfoList, deliveryStatus);

    // check if order is new one.
    boolean isNewOrder = true;
    if (last != null && orderDate != null) {
      // Fetched orderDate is greater than or equals to last one.
      int result = orderDate.compareTo((last.getOrderDate()));
      if (result >= 0) {
        isNewOrder = true;
      } else {
        isNewOrder = false;
      }
    }

    // check if fetched orderNumber equals to last one.
    boolean equalToLastOrder = false;
    if (last != null) {
      equalToLastOrder = orderNumber.equals(last.getOrderNumber());
    }
    // check if the order has already pushed in purchase history list.
    final String orderNumberTemp = orderNumber;
    boolean existOrderInList = list.stream().anyMatch(purchaseHistory -> purchaseHistory.getOrderNumber().equals(orderNumberTemp));

    if (isNewOrder && !equalToLastOrder && !existOrderInList) {
      list.add(ph);
    }

    return isNewOrder;
  }

  /**
   * Parse product info from an product element
   *
   * @param product DomNode for one product
   * @return product info
   */
  private ProductInfo parseProduct(DomNode product) {

    HtmlElement productAnchor = product.querySelector(property.getCrawling().getPurchaseHistoryListPage().getProductAnchor());
    String code        = parseProductCodeFromUrl(getAnchorHref(productAnchor));
    String name        = getTextContent(productAnchor);
    String distributor = getTextContent(product.querySelector(property.getCrawling().getPurchaseHistoryListPage().getProductDistributor()));
    String price       = getNumberAsStringFrom(product.querySelector(property.getCrawling().getPurchaseHistoryListPage().getUnitPrice()));
    String quantity    = getNumberAsStringFrom(product.querySelector(property.getCrawling().getPurchaseHistoryListPage().getProductQuantity()));
    // XPath Version query
    //String xxx       = product.getFirstByXPath(property.getCrawling().getPurchaseHistoryListPage().getXXX());

    if (StringUtils.isNotEmpty(distributor)) {
      distributor = distributor.split(":")[1].trim();
    }

    int quantityNum = 1;
    if (quantity != null) {
      quantityNum = Integer.valueOf(quantity);
    }

    return new ProductInfo(code, name, price, quantityNum, distributor);
  }

  private String parseProductCodeFromUrl(String url) {
    if (url == null) {
      return null;
    }
    Pattern pattern = Pattern.compile("\\/gp\\/product\\/([A-Z0-9]+)\\/");
    Matcher matcher = pattern.matcher(url);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }
  
  public static boolean isSessionExpired(HtmlPage page) {
    if (page == null) {
      return false;
    }
    try {
      HtmlForm signInForm = page.getFormByName("signIn");
      return signInForm != null;
    } catch (ElementNotFoundException e) {
      return false;
    }
  }
}
