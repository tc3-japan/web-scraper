package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.topcoder.common.model.scraper.PurchaseHistoryConfig;
import com.topcoder.common.model.scraper.PurchaseOrder;
import com.topcoder.common.model.scraper.PurchaseProduct;
import com.topcoder.common.repository.PurchaseHistoryRepository;
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

      List<DomNode> orderList = historyPage.scrapeDomList(purchaseHistoryConfig.getPurchaseOrder().getParent());
      processOrders(orderList);
      this.historyPage.setPage(this.gotoNextPage(this.historyPage.getPage(), webClient));
    }
  }

  /**
   * process purchase order
   *
   * @param orderList the order list
   */
  private void processOrders(List<DomNode> orderList) {
    LOGGER.debug("[processOrders] in");
    LOGGER.debug("[processOrders] Parsing page url " + historyPage.getPage().getUrl().toString());
    LOGGER.debug("[processOrders] Purchase list size =  " + orderList.size());

    PurchaseOrder orderConfig = purchaseHistoryConfig.getPurchaseOrder();
    for (DomNode orderNode : orderList) {
      this.currentPurchaseHistory = new PurchaseHistory();
      this.historyPage.setPurchaseHistory(this.currentPurchaseHistory);
      historyPage.scrapeOrderNumber(orderNode, orderConfig.getOrderNumber().getElement());

      // skip process is database exist
      if (!isNew()) {
        LOGGER.debug(String.format("[processOrders] [%s] order %s already exist, skip this",
            siteName, currentPurchaseHistory.getOrderNumber()));
        continue;
      }
      historyPage.scrapeOrderDate(orderNode, orderConfig.getOrderDate().getElement());
      historyPage.scrapeTotalAmount(orderNode, orderConfig.getTotalAmount().getElement());
      historyPage.scrapeDeliveryStatus(orderNode, orderConfig.getDeliveryStatus());

      List<DomNode> productList = historyPage.scrapeDomList(orderNode, orderConfig.getPurchaseProduct().getParent());
      processProducts(productList);
      this.purchaseHistoryList.add(this.currentPurchaseHistory);
    }
    LOGGER.info("[processOrders] done, size = " + this.purchaseHistoryList.size());
  }

  /**
   * process products in order row
   *
   * @param productList the product list
   */
  private void processProducts(List<DomNode> productList) {
    LOGGER.debug("[processProducts] in");

    PurchaseProduct productConfig = purchaseHistoryConfig.getPurchaseOrder().getPurchaseProduct();
    for (DomNode productNode : productList) {
      this.currentProduct = new ProductInfo();
      this.historyPage.setProductInfo(this.currentProduct);

      historyPage.scrapeProductCodeFromAnchor(productNode, productConfig.getProductCode().getElement(),
          productConfig.getProductCode().getRegex());
      historyPage.scrapeProductNameFromAnchor(productNode, productConfig.getProductName().getElement());
      historyPage.scrapeProductQuantity(productNode, productConfig.getProductQuantity().getElement());
      historyPage.scrapeUnitPrice(productNode, productConfig.getUnitPrice().getElement());
      historyPage.scrapeProductDistributor(productNode, productConfig.getProductDistributor().getElement());

      if (this.currentProduct.getName() != null) {
        this.currentPurchaseHistory.addProduct(this.currentProduct);
      }
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
