package com.topcoder.scraper.module.amazon.crawler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.topcoder.scraper.config.AmazonProperty;
import com.topcoder.scraper.model.ProductInfo;
import com.topcoder.scraper.model.PurchaseHistory;
import com.topcoder.scraper.service.WebpageService;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.topcoder.scraper.util.DateUtils.fromString;
import static com.topcoder.scraper.util.HtmlUtils.getAnchorHref;
import static com.topcoder.scraper.util.HtmlUtils.getTextContent;

/**
 * Crawl amazon purchase history page
 */
public class AmazonPurchaseHistoryListCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonPurchaseHistoryListCrawler.class);
  private final String siteName;
  private final AmazonProperty property;
  private final WebpageService webpageService;

  public AmazonPurchaseHistoryListCrawler(String siteName, AmazonProperty property, WebpageService webpageService) {
    this.siteName = siteName;
    this.property = property;
    this.webpageService = webpageService;
  }

  /**
   * Fetch purchase history
   * @param webClient the web client
   * @param lastPurchaseHistory optional, if it's empty, all orders will be fetched
   * @param saveHtml true if product html page will be saved
   * @return AmazonPurchaseHistoryListCrawlerResult
   * @throws IOException
   */
  public AmazonPurchaseHistoryListCrawlerResult fetchPurchaseHistoryList(WebClient webClient, PurchaseHistory lastPurchaseHistory, boolean saveHtml) throws IOException {
    List<PurchaseHistory> list = new LinkedList<>();
    List<String> pathList = new LinkedList<>();

    // go to homepage
    LOGGER.info("goto Home Page");
    HtmlPage homePage = webClient.getPage(property.getUrl());

    // go to order page
    LOGGER.info("goto Order Page");
    HtmlPage page = ((HtmlAnchor) homePage.querySelector(property.getCrawling().getHomePage().getOrdersButton())).click();
    // XPath Version query
    //String xxx  = homePage.getFirstByXPath(property.getCrawling().getHomePage().getXXX());

    while (true) {
      if (page == null || !parsePurchaseHistory(list, page, lastPurchaseHistory, saveHtml, pathList)) {
        break;
      } else {
        LOGGER.info("goto Next Page");
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
  private HtmlPage gotoNextPage(HtmlPage page, WebClient webClient) throws IOException {
    // Try to click next page first
    HtmlAnchor nextPageAnchor = page.querySelector(property.getCrawling().getPurchaseHistoryListPage().getNextPage());
    if (nextPageAnchor != null) {
      return nextPageAnchor.click();
    }

    // if pagination reaches end, try to go next time period
    HtmlSelect select = page.querySelector(property.getCrawling().getPurchaseHistoryListPage().getOrderFilter());
    // XPath Version query
    //String xxx      = page.getFirstByXPath(property.getCrawling().getPurchaseHistoryListPage().getXXX());

    if (select != null) {
      if (select.getSelectedIndex() + 1 < select.getOptionSize()) {
        String optionValue = select.getOption(select.getSelectedIndex() + 1).getValueAttribute();
        String optionLabel = select.getOption(select.getSelectedIndex() + 1).getText();
        LOGGER.info("goto " + optionLabel + ":" + optionValue + " Order Page");
        return webClient.getPage(property.getHistoryUrl() + optionValue);
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

    LOGGER.debug("Parsing page url %s", page.getUrl().toString());

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
    String price       = getTextContent(product.querySelector(property.getCrawling().getPurchaseHistoryListPage().getUnitPrice()));
    String quantity    = getTextContent(product.querySelector(property.getCrawling().getPurchaseHistoryListPage().getProductQuantity()));
    // XPath Version query
    //String xxx       = product.getFirstByXPath(property.getCrawling().getPurchaseHistoryListPage().getXXX());

    if (distributor != null) {
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
}
