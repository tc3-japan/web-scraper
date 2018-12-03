package com.topcoder.scraper.module.amazon;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.topcoder.scraper.config.AmazonProperty;
import com.topcoder.scraper.model.ProductInfo;
import com.topcoder.scraper.model.PurchaseHistory;
import com.topcoder.scraper.module.PurchaseHistoryListModule;
import com.topcoder.scraper.service.PurchaseHistoryService;
import com.topcoder.scraper.service.WebpageService;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.topcoder.scraper.util.DateUtils.fromString;
import static com.topcoder.scraper.util.HtmlUtils.getAnchorHref;
import static com.topcoder.scraper.util.HtmlUtils.getTextContent;

/**
 * Amazon implementation of PurchaseHistoryListModule
 */
@Component
public class AmazonPurchaseHistoryListModule extends PurchaseHistoryListModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonPurchaseHistoryListModule.class);

  private final AmazonProperty property;
  private final WebClient webClient;
  private final PurchaseHistoryService purchaseHistoryService;
  private final WebpageService webpageService;


  @Autowired
  public AmazonPurchaseHistoryListModule(
    AmazonProperty property,
    WebClient webClient,
    PurchaseHistoryService purchaseHistoryService,
    WebpageService webpageService) {
    this.property = property;
    this.webClient = webClient;
    this.purchaseHistoryService = purchaseHistoryService;
    this.webpageService = webpageService;
  }

  @Override
  public String getECName() {
    return "amazon";
  }

  /**
   * Implementation of fetchPurchaseHistoryList method
   */
  @Override
  public void fetchPurchaseHistoryList() throws IOException {

    Optional<PurchaseHistory> lastPurchaseHistory = purchaseHistoryService.fetchLast(getECName());

    List<PurchaseHistory> list = new LinkedList<>();

    // go to homepage
    LOGGER.info("goto Home Page");
    HtmlPage homePage = webClient.getPage(property.getUrl());

    // go to order page
    LOGGER.info("goto Order Page");
    //HtmlPage page = ((HtmlAnchor) homePage.getFirstByXPath("//*[@id=\"nav-orders\"]")).click();
    HtmlPage page = ((HtmlAnchor) homePage.querySelector("#nav-orders")).click();

    while (true) {
      if (page == null || !parsePurchaseHistory(list, page, lastPurchaseHistory)) {
        break;
      } else {
        LOGGER.info("goto Next Page");
        page = gotoNextPage(page);
      }
    }

    purchaseHistoryService.save(getECName(), list);
  }

  /**
   * check if next page button exist, or next time range is available
   *
   * @return next page if has next page
   */
  private HtmlPage gotoNextPage(HtmlPage page) throws IOException {
    // Try to click next page first
    //HtmlAnchor nextPageAnchor = page.getFirstByXPath("//*[@id=\"ordersContainer\"]/div[@class=\"a-row\"]/div/ul/li[@class=\"a-last\"]/a");
    HtmlAnchor nextPageAnchor = page.querySelector(property.getCrawling().getNextPage());
    if (nextPageAnchor != null) {
      return nextPageAnchor.click();
    }

    // if pagination reaches end, try to go next time period
    //HtmlForm form = page.getFirstByXPath("//*[@id=\"timePeriodForm\"]");
    HtmlForm form = page.querySelector("#timePeriodForm");
    if (form != null) {
      HtmlSelect select = form.getSelectByName("orderFilter");
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
    List<PurchaseHistory> list, HtmlPage page, Optional<PurchaseHistory> last) {

    LOGGER.debug("Parsing page url %s", page.getUrl().toString());

    //List<DomNode> orders = page.getByXPath("//*[@id=\"ordersContainer\"]/div[contains(@class, \"order\")]");
    List<DomNode> orders = page.querySelectorAll(property.getCrawling().getOrdersBox());

    boolean hasNewOrder = orders.stream().allMatch(order -> parseOrder(list, order, last));

    // only save purchase history page is there is new order
    if (hasNewOrder && orders.size() > 0) {
      webpageService.save("purchase-history", getECName(), page.getWebResponse().getContentAsString());
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
  private boolean parseOrder(List<PurchaseHistory> list, DomNode order, Optional<PurchaseHistory> last) {

    //String date           = getTextContent(order.getFirstByXPath(".//div[contains(@class, \"order-info\")]/div/div/div/div[1]/div/div[1]/div[2]/span"));
    //String total          = getTextContent(order.getFirstByXPath(".//div[contains(@class, \"order-info\")]/div/div/div/div[1]/div/div[2]/div[2]/span"));
    //String orderNumber    = getTextContent(order.getFirstByXPath(".//div[contains(@class, \"order-info\")]/div/div/div/div[2]/div[1]/span[2]"));
    //String deliveryStatus = getTextContent(order.getFirstByXPath(".//div[contains(@class, \"shipment\")]/div/div[1]/div[1]/div[2]/span[1]"));
    String date = getTextContent(order.querySelector(property.getCrawling().getOrderDate()));
    String total = getTextContent(order.querySelector(property.getCrawling().getTotalAmount()));
    String orderNumber = getTextContent(order.querySelector(property.getCrawling().getOrderNumber()));
    String deliveryStatus = getTextContent(order.querySelector(property.getCrawling().getDeliveryStatus()));

    //List<DomNode> products = order.getByXPath(".//div[contains(@class, \"shipment\")]/div/div/div/div[1]/div/div[contains(@class, \"a-fixed-left-grid\")]");
    List<DomNode> products = order.querySelectorAll(property.getCrawling().getProductsBox());

    List<ProductInfo> productInfoList = products.stream().map(this::parseProduct).collect(Collectors.toList());

    Date orderDate = null;
    try {
      orderDate = fromString(date);
    } catch (ParseException e) {
    }

    PurchaseHistory ph = new PurchaseHistory(property.getUsername(), orderNumber, orderDate, total, productInfoList, deliveryStatus);

    // check if order is new one.
    boolean isNewOrder = true;
    if (last.isPresent() && orderDate != null) {
      // Fetched orderDate is greater than or equals to last one.
      int result = orderDate.compareTo((last.get().getOrderDate()));
      if (result >= 0) {
        isNewOrder = true;
      } else {
        isNewOrder = false;
      }
    }

    // check if fetched orderNumber equals to last one.
    boolean equalToLastOrder = false;
    if (last.isPresent()) {
      equalToLastOrder = orderNumber.equals(last.get().getOrderNumber());
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

    //String name        = getTextContent(product.getFirstByXPath(".//div/div[2]/div[1]/a"));
    //String distributor = getTextContent(product.getFirstByXPath(".//span[contains(@class, \"a-color-secondary\")]"));
    //String price       = getTextContent(product.getFirstByXPath(".//span[contains(@class, \"a-color-price\")]"));
    //String quantity    = getTextContent(product.getFirstByXPath(".//span[contains(@class, \"item-view-qty\")]"));
    HtmlElement productAnchor = product.querySelector(property.getCrawling().getProductAnchor());
    String code = parseProductCodeFromUrl(getAnchorHref(productAnchor));
    String name = getTextContent(productAnchor);
    String distributor = getTextContent(product.querySelector(property.getCrawling().getProductDistributor()));
    String price = getTextContent(product.querySelector(property.getCrawling().getUnitPrice()));
    String quantity = getTextContent(product.querySelector(property.getCrawling().getProductQuantity()));

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
