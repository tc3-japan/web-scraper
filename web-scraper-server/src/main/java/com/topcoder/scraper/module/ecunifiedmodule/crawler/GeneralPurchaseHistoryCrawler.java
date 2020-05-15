package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.topcoder.common.model.scraper.PurchaseCommon;
import com.topcoder.common.model.scraper.PurchaseHistoryConfig;
import com.topcoder.common.model.scraper.PurchaseOrder;
import com.topcoder.common.model.scraper.PurchaseProduct;
import com.topcoder.common.repository.PurchaseHistoryRepository;
import com.topcoder.common.util.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.lib.navpage.NavigablePurchaseHistoryPage;
import com.topcoder.scraper.service.WebpageService;
import com.topcoder.common.dao.ConfigurationDAO;
import com.topcoder.common.repository.ConfigurationRepository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

public class GeneralPurchaseHistoryCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPurchaseHistoryCrawler.class);
  private String jsonConfigText = "";

  private List<String> savedPathList;
  private boolean saveHtml;

  @Getter
  @Setter
  private NavigablePurchaseHistoryPage historyPage;

  @Getter
  @Setter
  private TrafficWebClient webClient;

  @Getter
  @Setter
  private String siteName;

  @Getter
  @Setter
  private WebpageService webpageService;

  @Getter
  @Setter
  private PurchaseHistory currentPurchaseHistory; // OrderInfo (to be refactored)

  @Getter
  @Setter
  private ProductInfo currentProduct;

  @Getter
  @Setter
  private List<PurchaseHistory> purchaseHistoryList;

  private PurchaseHistoryConfig purchaseHistoryConfig;

  @Setter
  private PurchaseHistoryRepository historyRepository;

  public GeneralPurchaseHistoryCrawler(String siteName,
                                       WebpageService webpageService,
                                       ConfigurationRepository configurationRepository
  ) {
    LOGGER.debug("[constructor] in");
    this.siteName = siteName;
    this.webpageService = webpageService;
    this.jsonConfigText = this.getConfigFromDB(siteName, "purchase_history", configurationRepository);
  }

  /**
   * read json from database
   *
   * @param site                    the site name
   * @param type                    the json type
   * @param configurationRepository the database repository
   * @return json text
   */
  private String getConfigFromDB(String site, String type, ConfigurationRepository configurationRepository) {
    LOGGER.debug("[getConfigFromDB] in");
    LOGGER.debug("[getConfigFromDB] site:" + site + " type:" + type);
    ConfigurationDAO configurationDAO = configurationRepository.findBySiteAndType(site, type);
    return configurationDAO.getConfig();
  }

  /**
   * set config before run
   *
   * @param conf the config text
   */
  public void setConfig(String conf) {
    LOGGER.debug("[setConfig] in");
    LOGGER.debug("conf = " + conf);
    if (conf != null && !conf.equals("")) {
      this.jsonConfigText = conf;
    }
  }

  /**
   * fetch purchase history list
   *
   * @param webClient the web client
   * @param saveHtml  is need save html ?
   * @return result
   * @throws IOException if save html failed/parse json failed/get page failed
   */
  public GeneralPurchaseHistoryCrawlerResult fetchPurchaseHistoryList(TrafficWebClient webClient, boolean saveHtml) throws IOException {
    LOGGER.debug("[fetchPurchaseHistoryList] in");

    webClient.getWebClient().getOptions().setJavaScriptEnabled(true);
    this.webClient = webClient;
    this.saveHtml = saveHtml;
    this.historyPage = new NavigablePurchaseHistoryPage(this.webClient);

    this.purchaseHistoryList = new LinkedList<>();
    this.savedPathList = new LinkedList<>();

    purchaseHistoryConfig = new ObjectMapper().readValue(this.jsonConfigText, PurchaseHistoryConfig.class);
    historyPage.setPage(purchaseHistoryConfig.getUrl());
    processPurchaseHistory();

    return new GeneralPurchaseHistoryCrawlerResult(this.purchaseHistoryList, this.savedPathList);
  }

  /**
   * get all purchase history
   *
   * @throws IOException if save html failed
   */
  private void processPurchaseHistory() throws IOException {
    LOGGER.debug("[processPurchaseHistory] in");
    while (this.historyPage.getPage() != null) {

      String savedPath = this.webpageService.save(this.siteName + "-purchase-history", this.siteName, this.historyPage.getPage().getWebResponse().getContentAsString(), this.saveHtml);
      if (savedPath != null) {
        savedPathList.add(savedPath);
      }

      HtmlPage rootPage = historyPage.getPage();
      if (historyPage.isValid(purchaseHistoryConfig.getPurchaseOrder().getUrlElement())) {
        HtmlAnchor urlLink = historyPage.getPage().querySelector(purchaseHistoryConfig.getPurchaseOrder().getUrlElement());
        rootPage = urlLink.click();
      }
      List<DomNode> orderList = rootPage.querySelectorAll(purchaseHistoryConfig.getPurchaseOrder().getParent());
      processOrders(orderList, rootPage);
      this.historyPage.setPage(this.gotoNextPage(this.historyPage.getPage(), webClient));
    }
  }

  /**
   * process purchase order
   *
   * @param orderList the order list
   * @param rootPage  the page that need scrape
   */
  private void processOrders(List<DomNode> orderList, HtmlPage rootPage) throws IOException {
    LOGGER.debug("[processOrders] in");
    LOGGER.debug("[processOrders] Parsing page url " + historyPage.getPage().getUrl().toString());
    LOGGER.debug("[processOrders] Purchase list size =  " + orderList.size());

    PurchaseOrder orderConfig = purchaseHistoryConfig.getPurchaseOrder();
    for (DomNode orderNode : orderList) {
      this.currentPurchaseHistory = new PurchaseHistory();
      this.historyPage.setPurchaseHistory(this.currentPurchaseHistory);
      scrapeOrder(rootPage, orderNode, orderConfig, currentPurchaseHistory);
      // skip process is database exist
      if (!isNew()) {
        LOGGER.debug(String.format("[processOrders] [%s] order %s already exist, skip this",
            siteName, currentPurchaseHistory.getOrderNumber()));
        continue;
      }

      HtmlPage orderPage = rootPage;
      List<DomNode> productList;
      if (historyPage.isValid(orderConfig.getPurchaseProduct().getUrlElement())) {
        HtmlAnchor anchor = orderNode.querySelector(orderConfig.getPurchaseProduct().getUrlElement());
        orderPage = anchor.click();
        // fetch product by page root
        productList = orderPage.querySelectorAll(orderConfig.getPurchaseProduct().getParent());
      } else {
        // fetch product by order node
        productList = orderNode.querySelectorAll(orderConfig.getPurchaseProduct().getParent());
      }

      LOGGER.debug("[processOrders] productList.size() = " + productList.size());

      // On the contrast if a field for purchase_product appears under purchase_order, please reuse and set the scraped value to purchase_product
      ProductInfo reuseProduct = scrapeProduct(orderPage, orderNode, orderConfig);
      processProducts(productList, orderPage, reuseProduct);
      this.purchaseHistoryList.add(this.currentPurchaseHistory);
    }
    LOGGER.info("[processOrders] done, size = " + this.purchaseHistoryList.size());
  }


  /**
   * scrape order
   * @param urlElementPage the root page
   * @param orderNode the order node
   * @param config the selector config
   * @param history the history item
   */
  private void scrapeOrder(HtmlPage urlElementPage, DomNode orderNode, PurchaseCommon config, PurchaseHistory history) {
    if (config.getOrderNumber() != null) {
      history.setOrderNumber(historyPage.scrapeString(urlElementPage, orderNode, config.getOrderNumber()));
    }
    if (config.getOrderDate() != null) {
      history.setOrderDate(historyPage.scrapeDate(urlElementPage, orderNode, config.getOrderDate()));
    }
    if (config.getTotalAmount() != null) {
      Float totalAmount = historyPage.scrapeFloat(urlElementPage, orderNode, config.getTotalAmount());
      history.setTotalAmount(totalAmount == null ? null : totalAmount.toString());
    }
    if (config.getDeliveryStatus() != null) {
      history.setDeliveryStatus(historyPage.scrapeString(urlElementPage, orderNode, config.getDeliveryStatus()));
    }
  }

  /**
   * scrape product
   * @param orderPage the page
   * @param productNode the product node
   * @param config the product selector config
   * @return the product
   */
  private ProductInfo scrapeProduct(HtmlPage orderPage, DomNode productNode, PurchaseCommon config) {
    ProductInfo info = new ProductInfo();
    if (config.getProductCode() != null) {
      info.setCode(historyPage.scrapeString(orderPage, productNode, config.getProductCode()));
    }
    if (config.getProductName() != null) {
      info.setName(historyPage.scrapeString(orderPage, productNode, config.getProductName()));
    }
    if (config.getProductQuantity() != null) {
      Float quantity = historyPage.scrapeFloat(orderPage, productNode, config.getProductQuantity());
      info.setQuantity(quantity == null ? null : quantity.intValue());
    }
    if (config.getUnitPrice() != null) {
      Float price = historyPage.scrapeFloat(orderPage, productNode, config.getUnitPrice());
      info.setPrice(price == null ? null : price.toString());
    }
    if (config.getProductDistributor() != null) {
      info.setDistributor(historyPage.scrapeString(orderPage, productNode, config.getProductDistributor()));
    }
    return info;
  }

  /**
   * process products in order row
   *
   * @param productList  the product list
   * @param orderPage    the order page, this order page maybe is root history page
   * @param reuseProduct the reuse product
   */
  private void processProducts(List<DomNode> productList, HtmlPage orderPage, ProductInfo reuseProduct) throws IOException {
    LOGGER.debug("[processProducts] in");

    PurchaseProduct productConfig = purchaseHistoryConfig.getPurchaseOrder().getPurchaseProduct();
    for (DomNode productNode : productList) {
      this.currentProduct = scrapeProduct(orderPage, productNode, productConfig);
      BeanUtils.copyProperties(reuseProduct, currentProduct, Common.getNullPropertyNames(reuseProduct));

      if (this.currentProduct.getName() != null) {
        this.currentPurchaseHistory.addProduct(this.currentProduct);
      }
      //if a field for purchase_order appears under purchase_product, please set the scraped value to purchase_order.
      scrapeOrder(orderPage, productNode, productConfig, currentPurchaseHistory);
    }
  }

  /**
   * check is in database or not
   *
   * @return result
   */
  protected boolean isNew() {
    return historyRepository == null
        || historyRepository.getByEcSiteAndOrderNo(siteName, currentPurchaseHistory.getOrderNumber()) == null;
  }

  /**
   * goto next page
   *
   * @param page      the html page
   * @param webClient the web client
   * @return next page
   * @throws IOException page get failed
   */
  private HtmlPage gotoNextPage(HtmlPage page, TrafficWebClient webClient) throws IOException {
    LOGGER.debug("[gotoNextPage] in");
    // Try to click next page first
    HtmlAnchor nextPageAnchor = page.querySelector(purchaseHistoryConfig.getNextUrlElement());
    if (nextPageAnchor != null) {
      LOGGER.info("[gotoNextPage] goto Next Page");
      return nextPageAnchor.click();
    }
    return null;
  }
}
