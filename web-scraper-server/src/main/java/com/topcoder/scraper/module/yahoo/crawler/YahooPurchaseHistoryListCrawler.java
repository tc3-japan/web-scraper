package com.topcoder.scraper.module.yahoo.crawler;

import static com.topcoder.common.util.HtmlUtils.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.api.service.login.yahoo.YahooLoginHandler;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.DateUtils;
import com.topcoder.scraper.exception.SessionExpiredException;
import com.topcoder.scraper.service.WebpageService;

public class YahooPurchaseHistoryListCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooPurchaseHistoryListCrawler.class);
  private static final Pattern PAT_ORDER_NO = Pattern.compile("([\\d]{13})", Pattern.DOTALL);
  private final String siteName;
  private final WebpageService webpageService;

  public YahooPurchaseHistoryListCrawler(String siteName, WebpageService webpageService) {
    this.siteName = siteName;
    this.webpageService = webpageService;     
  }

  public YahooPurchaseHistoryListCrawlerResult fetchPurchaseHistoryList(TrafficWebClient webClient, PurchaseHistory lastPurchaseHistory, boolean saveHtml) throws IOException {
    List<PurchaseHistory> list = new LinkedList<>();
    List<String> pathList = new LinkedList<>();
    LOGGER.info("goto Order History Page");

    //TODO: LOGIN page: https://login.yahoo.co.jp/config/login?.src=shp&lg=jp&.intl=jp&.done=https%3A%2F%2Fshopping.yahoo.co.jp%2F%3Fsc_e%3Dytmh
    //TODO: Purchase History Page: https://odhistory.shopping.yahoo.co.jp/cgi-bin/history-list?sc_i=shp_pc_top_MHD_order_history
    //HtmlPage page = webClient.getPage("https://www.kojima.net/ec/member/CMmOrderHistory.jsp");
    HtmlPage page = webClient.getPage("https://odhistory.shopping.yahoo.co.jp/cgi-bin/history-list?sc_i=shp_pc_top_MHD_order_history");
    if (page.getBaseURI().contains("?autoLogin")) {
      throw new SessionExpiredException("Session has been expired.");     //TODO: Is this relevent? 
    }

    webpageService.save("yahoo-purchase-history", siteName, page.getWebResponse().getContentAsString());
    while (true) {
      if (page == null || !parsePurchaseHistory(list, page, webClient, webpageService, lastPurchaseHistory, saveHtml, pathList)) {
        break;
      }
      page = gotoNextPage(page, webClient);
    }

    return new YahooPurchaseHistoryListCrawlerResult(list, pathList);
  }
  
  private boolean parsePurchaseHistory(List<PurchaseHistory> list, HtmlPage page, TrafficWebClient webClient, WebpageService webpageService, PurchaseHistory last, boolean saveHtml, List<String> pathList) {

    LOGGER.debug("Parsing page url " + page.getUrl().toString());
    System.out.println("\n\n\n>>>> Fakely parsing purchase history\n\n\n");
    
    //member-orderhistorydetails
    //List<DomNode> orders = page.querySelectorAll(".member-orderhistorydetails > tbody");

    List<DomNode> orders = new ArrayList<DomNode>();
    boolean nullIndexFound = false;
    int index = 1;
    
    while (!nullIndexFound) {
      DomNode result = page.querySelector(".elMain > ul:nth-child(1) > li:nth-child(" + Integer.toString(index) + ")");
      System.out.println(result);
      if (result!=null) {
        orders.add(result);
      } else {
        nullIndexFound = true;
      }
      index++;
    }
    
    if (orders!=null) {
      for (DomNode orderNode : orders) {
        System.out.println("*******");
      
        DomNode itemNoNode = orderNode.querySelector("div:nth-child(2) > ul:nth-child(1) > li:nth-child(1) > ul:nth-child(1) > li:nth-child(2) > dl:nth-child(1) > dd:nth-child(2)");
        //String nodeText = itemNoNode.asText();
        String orderNumber = itemNoNode.asText(); //extract(nodeText, PAT_ORDER_NO);
        LOGGER.info("ORDER NO: " + orderNumber);

        String itemDate = orderNode.querySelector("div:nth-child(1) > p:nth-child(1) > span:nth-child(1)").asText();
        Date orderDate = extractDate(itemDate);
        LOGGER.info("ORDER DATE: " + orderDate);
      
        //Follow the link to 

        DomElement orderInfoPage = orderNode.querySelector("div:nth-child(2) > ul:nth-child(1) > li:nth-child(1) > ul:nth-child(3) > li:nth-child(1) > a:nth-child(1) > span:nth-child(1)");
        HtmlPage detailPage = null;
        try{
          HtmlPage needToLoginPage = webClient.click(orderInfoPage); //go get price>
          //webpageService.save("yahoo-login-initial", siteName, loginPage.getWebResponse().getContentAsString());
          //webpageService.save("yahoo-purchase-history-detail-login", "yahoo", detailLoginPage.getWebResponse().getContentAsString());
          HtmlPage passwordPage = webClient.click(needToLoginPage.querySelector("p.elButton:nth-child(3) > a:nth-child(1) > span:nth-child(1)"));
          webpageService.save("yahoo-type-password-page", "yahoo", passwordPage.getWebResponse().getContentAsString());

          //TODO: Type password
          //TODO: See this page file:///home/----/web-scraper/web-scraper-server/logs/yahoo/yahoo-purchase-history-detail-page-2019-07-23T19-19-43.586.html

        } catch (Exception e) {}
        

        //DomNode itemTotalAmountNode = orderNode.querySelector(".totalamountmoney");
        //Integer totalAmount = itemTotalAmountNode != null ? extractInt(itemTotalAmountNode.asText()) : null;
        //LOGGER.info("TOTAL: " + totalAmount);
        Integer totalAmount = 999999999; //TODO: Get real amount!

        PurchaseHistory history = new PurchaseHistory(null, orderNumber, orderDate, totalAmount != null ? totalAmount.toString() : null, null, null);
        if (!isNew(history, last)) {
          LOGGER.info("SKIPPING: " + orderNumber);
          continue;        
        }
      
        List<DomNode> orderLines = orderNode.querySelectorAll("tr");
        List<ProductInfo> productInfoList = orderLines.stream().map(this::parseProduct).filter(p -> p.getName() != null).collect(Collectors.toList());
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

    if ((orderDate != null && lastOrderDate != null && orderDate.compareTo(lastOrderDate) <= 0) || lastOrderNo.equals(orderNumber)) {
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
    return new ProductInfo((String)null, name, price != null ? price.toString() : null, quantity, (String)null);
  }
  
  private HtmlPage gotoNextPage(HtmlPage page, TrafficWebClient webClient) throws IOException {
    return null;
  }
  
  
  //private static final Pattern PAT_DATE = Pattern.compile("(20[\\d]{2}/[\\d]{2}/[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2})", Pattern.DOTALL);
  //private static final String FORMAT_DATE = "yyyy/MM/dd HH:mm:ss";
  
  private String normalizeProductName(String productName) {
    if (productName == null) {
      return productName;
    }
    return productName.trim().replaceAll("　", " ");
  }
  
  private Date extractDate(String text) {
    String dateStr = text;//extract(text, PAT_DATE);
    try {
      return DateUtils.fromString(dateStr);
    } catch (ParseException e) {
      LOGGER.error(String.format("Failed to parse the input '%s'. Error: %s", dateStr, e.getMessage()));
      e.printStackTrace();
      return null;
    }
  }
}
