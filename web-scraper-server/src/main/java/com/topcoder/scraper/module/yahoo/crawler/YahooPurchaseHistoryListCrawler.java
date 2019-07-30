package com.topcoder.scraper.module.yahoo.crawler;

import static com.topcoder.common.util.HtmlUtils.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.DateUtils;
import com.topcoder.scraper.service.WebpageService;

public class YahooPurchaseHistoryListCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooPurchaseHistoryListCrawler.class);
  //private static final Pattern PAT_ORDER_NO = Pattern.compile("([\\d]{13})", Pattern.DOTALL); //TODO: Do we need this? What is it for?
  private final String siteName;
  private final WebpageService webpageService;
  private ECSiteAccountDAO ecSiteAccountDAO;

  //for testing only
  private String testEmail;
  private String testPassword;

  public YahooPurchaseHistoryListCrawler(String siteName, WebpageService webpageService,
      ECSiteAccountDAO ecSiteAccountDAO) {
    this.siteName = siteName;
    this.webpageService = webpageService;
    this.ecSiteAccountDAO = ecSiteAccountDAO;
  }

  public YahooPurchaseHistoryListCrawler(String siteName, WebpageService webpageService, String testEmail, String testPassword) {
    this.siteName = siteName;
    this.webpageService = webpageService;
    this.testEmail = testEmail;
    this.testPassword = testPassword;
  }

  public YahooPurchaseHistoryListCrawlerResult fetchPurchaseHistoryList(TrafficWebClient webClient,
      PurchaseHistory lastPurchaseHistory, boolean saveHtml) throws IOException {
    List<PurchaseHistory> list = new LinkedList<>();
    List<String> pathList = new LinkedList<>();
    LOGGER.info("goto Order History Page");

    HtmlPage page = webClient.getPage("https://odhistory.shopping.yahoo.co.jp/cgi-bin/history-list?sc_i=shp_pc_top_MHD_order_history");
    
    /*
    if (page.getBaseURI().contains("?autoLogin")) {
      throw new SessionExpiredException("Session has been expired."); // TODO: Is this relevent? Do we need to implement it?
    }
    */

    webpageService.save("yahoo-purchase-history", siteName, page.getWebResponse().getContentAsString());
    while (true) {
      if (page == null
          || !parsePurchaseHistory(list, page, webClient, webpageService, lastPurchaseHistory, saveHtml, pathList)) {
        break;
      }
      page = gotoNextPage(page, webClient);
    }

    return new YahooPurchaseHistoryListCrawlerResult(list, pathList);
  }

  private boolean parsePurchaseHistory(List<PurchaseHistory> list, HtmlPage page, TrafficWebClient webClient,
      WebpageService webpageService, PurchaseHistory last, boolean saveHtml, List<String> pathList) {

    LOGGER.debug("Parsing page url " + page.getUrl().toString());

    List<DomNode> orders = new ArrayList<DomNode>();
    boolean nullIndexFound = false;
    int index = 1;

    while (!nullIndexFound) {
      DomNode result = page.querySelector(".elMain > ul:nth-child(1) > li:nth-child(" + Integer.toString(index) + ")");
      if (result != null) {
        orders.add(result);
      } else {
        nullIndexFound = true;
      }
      index++;
    }

    if (orders != null) {
      for (DomNode orderNode : orders) { //TODO: We may need to iterate over items within orders? Not sure how they're displayed

        DomNode itemNoNode = orderNode.querySelector("div:nth-child(2) > ul:nth-child(1) > li:nth-child(1) > ul:nth-child(1) > li:nth-child(2) > dl:nth-child(1) > dd:nth-child(2)");
        String orderNumber = itemNoNode.asText();
        LOGGER.info("ORDER NO: " + orderNumber);

        String itemDate = orderNode.querySelector("div:nth-child(1) > p:nth-child(1) > span:nth-child(1)").asText();
        Date orderDate = extractDate(itemDate);
        LOGGER.info("ORDER DATE: " + orderDate);

        DomElement orderInfoLink = orderNode.querySelector("div:nth-child(2) > ul:nth-child(1) > li:nth-child(1) > ul:nth-child(3) > li:nth-child(1) > a:nth-child(1) > span:nth-child(1)");

        Integer totalAmount = null;
        DomNode totalAmountNode = null;
        try {
          HtmlPage orderInfoPage = webClient.click(orderInfoLink);
          totalAmountNode = orderInfoPage.querySelector("#total > ul:nth-child(2) > li:nth-child(4) > dl:nth-child(1) > dd:nth-child(2)");
        } catch (Exception e) {}
        
        if (totalAmountNode != null) {
          totalAmount = extractInt(totalAmountNode.asText());
        } else {
          try {
            HtmlPage needToLoginPage = webClient.click(orderInfoLink); // go get price>
            HtmlPage loginPage = webClient.click(needToLoginPage.querySelector("p.elButton:nth-child(3) > a:nth-child(1) > span:nth-child(1)"));
            webpageService.save("yahoo-type-login-page", "yahoo", loginPage.getWebResponse().getContentAsString());

            String username;
            String password;

            if (ecSiteAccountDAO != null) {
              username = ecSiteAccountDAO.getLoginEmail();
              password = ecSiteAccountDAO.getPassword();
            } else {
              username = testEmail;
              password = testPassword;
            }

            // Warning: Username comes on first page unless autologin / remember me
            HtmlTextInput memberIdInput = loginPage.querySelector("#username");
            memberIdInput.type(username);
            HtmlButton nextButton = loginPage.querySelector("#btnNext");
            HtmlPage passwordPage2 = webClient.click(nextButton);


            if (passwordPage2 != null) { //TODO: This may not be necessary after all?
            } else {} // else use login page because username was already remembered and you were at
              // password login page not user email login page
            HtmlPasswordInput passwordInput = passwordPage2.querySelector("#passwd");
            HtmlCheckBoxInput rememberInput = passwordPage2.querySelector("#persistent");
            HtmlButton loginButtonInput = passwordPage2.querySelector("#btnSubmit");

            passwordInput.type(password);
            rememberInput.type("off");
            HtmlPage afterLoginPage = webClient.click(loginButtonInput);
            HtmlButton skipThisPageButton = afterLoginPage.querySelector("#skipButton");
            HtmlPage finalPage;
            if (skipThisPageButton != null) { //This may or may not be the final page.
              finalPage = webClient.click(skipThisPageButton);
            } else {
              finalPage = afterLoginPage;
            }
            webpageService.save("yahoo-final-page", siteName, finalPage.getWebResponse().getContentAsString());
            totalAmountNode = finalPage.querySelector("#total > ul:nth-child(2) > li:nth-child(4) > dl:nth-child(1) > dd:nth-child(2)");
            totalAmount = totalAmountNode != null ? extractInt(totalAmountNode.asText()) : null;

          } catch (Exception e) {System.out.println(e.getMessage());}
        }

        PurchaseHistory history = new PurchaseHistory(null, orderNumber, orderDate,
            totalAmount != null ? totalAmount.toString() : null, null, null);
        if (!isNew(history, last)) {
          LOGGER.info("SKIPPING: " + orderNumber);
          continue;
        }

        List<DomNode> orderLines = orderNode.querySelectorAll("tr");
        List<ProductInfo> productInfoList = orderLines.stream().map(this::parseProduct).filter(p -> p.getName() != null)
            .collect(Collectors.toList());
        history.setProducts(productInfoList);

        list.add(history);

      }
    }

    return false;
  }

  private boolean isNew(PurchaseHistory purchase, PurchaseHistory last) {
    Date orderDate = purchase.getOrderDate();
    Date lastOrderDate = last != null ? last.getOrderDate() : null;
    String orderNumber = purchase.getOrderNumber();
    String lastOrderNo = last != null ? (last.getOrderNumber() != null ? last.getOrderNumber() : "") : "";

    if ((orderDate != null && lastOrderDate != null && orderDate.compareTo(lastOrderDate) <= 0)
        || lastOrderNo.equals(orderNumber)) {
      return false;
    }
    return true;
  }

  private ProductInfo parseProduct(DomNode orderLineNode) {

    DomNode itemNameNode = orderLineNode.querySelector(".itemname");
    if (itemNameNode == null) {
      return new ProductInfo();
    }
    String name = normalizeProductName(itemNameNode.asText());

    DomNode itemPriceNode = orderLineNode.querySelector(".itemprice");
    Integer price = itemPriceNode != null ? extractInt(itemPriceNode.asText()) : null;

    DomNode itemQtyNode = orderLineNode.querySelector(".itemnum");
    Integer quantity = itemQtyNode != null ? extractInt(itemQtyNode.asText()) : null;

    LOGGER.info(String.format("parseProduct::{Name:%s, Price:%d, Quantity:%s}", name, price, quantity));
    return new ProductInfo((String) null, name, price != null ? price.toString() : null, quantity, (String) null);
  }

  private HtmlPage gotoNextPage(HtmlPage page, TrafficWebClient webClient) throws IOException {
    return null;
  }

  private String normalizeProductName(String productName) {
    if (productName == null) {
      return productName;
    }
    return productName.trim().replaceAll("　", " ");
  }

  private Date extractDate(String text) {
    String dateStr = text;// extract(text, PAT_DATE);
    try {
      return DateUtils.fromString(dateStr);
    } catch (ParseException e) {
      LOGGER.error(String.format("Failed to parse the input '%s'. Error: %s", dateStr, e.getMessage()));
      e.printStackTrace();
      return null;
    }
  }
}
