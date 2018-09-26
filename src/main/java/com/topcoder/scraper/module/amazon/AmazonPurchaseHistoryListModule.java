package com.topcoder.scraper.module.amazon;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.topcoder.scraper.config.AmazonProperty;
import com.topcoder.scraper.model.ProductInfo;
import com.topcoder.scraper.model.PurchaseHistory;
import com.topcoder.scraper.module.PurchaseHistoryListModule;
import com.topcoder.scraper.service.PurchaseHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.topcoder.scraper.util.DateUtils.fromString;

/**
 * Amazon implementation of PurchaseHistoryListModule
 */
@Component
public class AmazonPurchaseHistoryListModule extends PurchaseHistoryListModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonPurchaseHistoryListModule.class);

  private final AmazonProperty property;
  private final WebClient webClient;
  private final PurchaseHistoryService purchaseHistoryService;

  @Autowired
  public AmazonPurchaseHistoryListModule(
    AmazonProperty property,
    WebClient webClient,
    PurchaseHistoryService purchaseHistoryService) {
    this.property = property;
    this.webClient = webClient;
    this.purchaseHistoryService = purchaseHistoryService;
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
    HtmlPage page = ((HtmlAnchor) homePage.getFirstByXPath("//*[@id=\"nav-orders\"]")).click();

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
   * @return next page if has next page
   */
  private HtmlPage gotoNextPage(HtmlPage page) throws IOException {
    // Try to click next page first
    HtmlAnchor nextPageAnchor = page.getFirstByXPath("//*[@id=\"ordersContainer\"]/div[@class=\"a-row\"]/div/ul/li[@class=\"a-last\"]/a");
    if (nextPageAnchor != null) {
      return nextPageAnchor.click();
    }

    // if pagination reaches end, try to go next time period
    HtmlForm form = page.getFirstByXPath("//*[@id=\"timePeriodForm\"]");
    if (form != null) {
      HtmlSelect select = form.getSelectByName("orderFilter");
      if (select.getSelectedIndex() + 1 < select.getOptionSize()) {
        String optionValue = select.getOption(select.getSelectedIndex() + 1).getValueAttribute();
        String optionLabel = select.getOption(select.getSelectedIndex() + 1).getText();
        LOGGER.info("goto " + optionLabel + " Order Page");
        return webClient.getPage(property.getHistoryUrl() + optionValue);
      }
    }

    return null;
  }

  /**
   * Parse purchase history from webpage
   * @param list purchase history list
   * @param page html page
   * @param last last purchase history
   * @return true if all orders are new, requires checking next page
   */
  private boolean parsePurchaseHistory(
    List<PurchaseHistory> list, HtmlPage page, Optional<PurchaseHistory> last) {

    LOGGER.debug("Parsing page url %s", page.getUrl().toString());

    List<DomNode> orders = page.getByXPath("//*[@id=\"ordersContainer\"]/div[contains(@class, \"order\")]");
    return orders.stream().allMatch(order -> parseOrder(list, order, last));
  }

  /**
   * Parse purchase history from an order element
   *
   * @param list purchase history list
   * @param order DomNode for one order
   * @param last last purchase history
   * @return purchase history
   */
  private boolean parseOrder(List<PurchaseHistory> list, DomNode order, Optional<PurchaseHistory> last) {

    String date = ((HtmlSpan) order.getFirstByXPath(".//div[contains(@class, \"order-info\")]/div/div/div/div[1]/div/div[1]/div[2]/span")).getTextContent().trim();
    String total = ((HtmlSpan) order.getFirstByXPath(".//div[contains(@class, \"order-info\")]/div/div/div/div[1]/div/div[2]/div[2]/span")).getTextContent().trim();
    String orderNumber = ((HtmlSpan) order.getFirstByXPath(".//div[contains(@class, \"order-info\")]/div/div/div/div[2]/div[1]/span[2]")).getTextContent().trim();

    List<DomNode> products = order.getByXPath(".//div[contains(@class, \"shipment\")]/div/div/div/div[1]/div/div[contains(@class, \"a-fixed-left-grid\")]");

    List<ProductInfo> productInfoList = products.stream().map(this::parseProduct).collect(Collectors.toList());

    PurchaseHistory ph = new PurchaseHistory(orderNumber, date, total.substring(1), productInfoList, null);

    boolean isNewOrder = true;

    if (last.isPresent()) {
      try {
        isNewOrder = fromString(date).compareTo(fromString(last.get().getOrderDate())) > 0;
      } catch (ParseException e) {
        // fail to parse date, skip
      }
      if (isNewOrder) {
        list.add(ph);
      }
    } else {
      list.add(ph);
    }

    return isNewOrder;
  }

  /**
   * Parse product info from an product element
   * @param product DomNode for one product
   * @return product info
   */
  private ProductInfo parseProduct(DomNode product) {

    String name = ((HtmlAnchor) product.getFirstByXPath(".//div/div[2]/div[1]/a")).getTextContent().trim();
    String distributor = ((HtmlSpan) product.getFirstByXPath(".//span[contains(@class, \"a-color-secondary\")]")).getTextContent().split(":")[1].trim();
    String price = ((HtmlSpan) product.getFirstByXPath(".//span[contains(@class, \"a-color-price\")]")).getTextContent().trim();

    HtmlSpan quantitySpan = product.getFirstByXPath(".//span[contains(@class, \"item-view-qty\")]");
    String quantity = "1";
    if (quantitySpan != null) {
      quantity = quantitySpan.getTextContent().trim();
    }

    return new ProductInfo(name, price.substring(1), quantity, distributor);


  }
}
