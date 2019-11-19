package com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler;

import static com.topcoder.common.util.HtmlUtils.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.lib.navpage.NavigablePurchaseHistoryPage;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralPurchaseHistoryListCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralPurchaseHistoryListCrawlerResult;
import com.topcoder.scraper.service.WebpageService;

public class OldYahooPurchaseHistoryListCrawler extends GeneralPurchaseHistoryListCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(OldYahooPurchaseHistoryListCrawler.class);

  private final String siteName;
  private final WebpageService webpageService;

  // for testing only
  private String username; // Actually email?
  private String password;

  public OldYahooPurchaseHistoryListCrawler(String siteName, WebpageService webpageService,
                                            ECSiteAccountDAO ecSiteAccountDAO) {
    super(siteName, webpageService);
    this.siteName = siteName;
    this.webpageService = webpageService;
    this.username = ecSiteAccountDAO.getLoginEmail();
    this.password = ecSiteAccountDAO.getPassword();
  }

  public OldYahooPurchaseHistoryListCrawler(String siteName, WebpageService webpageService, String username,
                                            String password) {
    super(siteName, webpageService);
    this.siteName = siteName;
    this.webpageService = webpageService;
    this.username = username;
    this.password = password;
  }

  public GeneralPurchaseHistoryListCrawlerResult fetchPurchaseHistoryList(TrafficWebClient webClient,
                                                                          PurchaseHistory lastPurchaseHistory, boolean saveHtml) throws IOException {
    List<PurchaseHistory> list = new LinkedList<>();
    List<String> pathList = new LinkedList<>();
    LOGGER.info("goto Order History Page");

    HtmlPage page = webClient
        .getPage("https://odhistory.shopping.yahoo.co.jp/cgi-bin/history-list?sc_i=shp_pc_top_MHD_order_history");

    webpageService.save("yahoo-purchase-history", siteName, page.getWebResponse().getContentAsString());
    while (true) {
      if (page == null
          || !parsePurchaseHistory(list, page, webClient, webpageService, lastPurchaseHistory, saveHtml, pathList)) {
        break;
      }
      //page = gotoNextPage(page, webClient);
    }

    return new GeneralPurchaseHistoryListCrawlerResult(list, pathList);
  }

  private boolean parsePurchaseHistory(List<PurchaseHistory> list, HtmlPage page, TrafficWebClient webClient,
      WebpageService webpageService, PurchaseHistory last, boolean saveHtml, List<String> pathList) {
 
    //LOGGER.debug("Parsing page url " + page.getUrl().toString());

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
      for (DomNode orderNode : orders) { // TODO: We may need to iterate over items within orders? Not sure how they're
                                         // displayed

        PurchaseHistory purchaseHistory = new PurchaseHistory();
        NavigablePurchaseHistoryPage historyPage = new NavigablePurchaseHistoryPage(page, webClient);

        // TODO: fix
        //historyPage.setOrderNumber(orderNode, "div:nth-child(2) > ul:nth-child(1) > li:nth-child(1) > ul:nth-child(1) > li:nth-child(2) > dl:nth-child(1) > dd:nth-child(2)");
        //historyPage.setOrderDate(orderNode, "div:nth-child(1) > p:nth-child(1) > span:nth-child(1)");

        historyPage.click(orderNode, "div:nth-child(2) > ul:nth-child(1) > li:nth-child(1) > ul:nth-child(3) > li:nth-child(1) > a:nth-child(1) > span:nth-child(1)");
        historyPage.savePage("yahoo-after-click-login", "yahoo", webpageService);

        historyPage.click("p.elButton:nth-child(3) > a:nth-child(1) > span:nth-child(1)");
        historyPage.type(username, "#username");
        historyPage.click("#btnNext", webpageService);
        historyPage.typePassword(password, "#passwd"); //or #code
        historyPage.typeCheckbox("off", "#persistent");
        historyPage.click("#btnSubmit", webpageService);
        // TODO: fix
        //historyPage.setPrice("#total > ul:nth-child(2) > li:nth-child(4) > dl:nth-child(1) > dd:nth-child(2)");

        if (!isNew()) {
          LOGGER.info("SKIPPING: " + historyPage.getPurchaseHistory().getOrderNumber());
          continue;
        }

        List<DomNode> orderLines = orderNode.querySelectorAll("tr");
        List<ProductInfo> productInfoList = orderLines.stream().map(this::parseProduct).filter(p -> p.getName() != null)
            .collect(Collectors.toList());
        historyPage.getPurchaseHistory().setProducts(productInfoList);

        list.add(historyPage.getPurchaseHistory());

      }
    }

    return false;
  }




  
  private ProductInfo parseProduct(DomNode orderLineNode) {

    DomNode itemNameNode = orderLineNode.querySelector(".itemname");
    if (itemNameNode == null) {
      return new ProductInfo();
    }
    //String name = normalizeProductName(itemNameNode.asText());
    String name = itemNameNode.asText();

    DomNode itemPriceNode = orderLineNode.querySelector(".itemprice");
    Integer price = itemPriceNode != null ? extractInt(itemPriceNode.asText()) : null;

    DomNode itemQtyNode = orderLineNode.querySelector(".itemnum");
    Integer quantity = itemQtyNode != null ? extractInt(itemQtyNode.asText()) : null;

    LOGGER.info(String.format("parseProduct::{Name:%s, Price:%d, Quantity:%s}", name, price, quantity));
    return new ProductInfo((String) null, name, price != null ? price.toString() : null, quantity, (String) null);
  }

}
