package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.model.scraper.PurchaseCommon;
import com.topcoder.common.model.scraper.PurchaseHistoryConfig;
import com.topcoder.common.model.scraper.PurchaseOrder;
import com.topcoder.common.model.scraper.PurchaseProduct;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.repository.PurchaseHistoryRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.Common;
import com.topcoder.scraper.lib.navpage.NavigablePage;
import com.topcoder.scraper.lib.navpage.NavigablePurchaseHistoryPage;
import com.topcoder.scraper.module.ecunifiedmodule.dryrun.DryRunUtils;
import com.topcoder.scraper.service.WebpageService;

import lombok.Getter;
import lombok.Setter;

public class GeneralPurchaseHistoryCrawler extends AbstractGeneralCrawler {

    private class DuplicatedException extends Exception {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPurchaseHistoryCrawler.class);

    private List<String> savedPathList;

    private List<String> scrapedPageList;
    private List<String> scrapedOrderNumberList;

    @Getter
    @Setter
    private NavigablePurchaseHistoryPage historyPage;

    @Getter
    @Setter
    private TrafficWebClient webClient;

    @Getter
    @Setter
    private ProductInfo currentProduct;

    @Getter
    @Setter
    private List<PurchaseHistory> purchaseHistoryList;

    private PurchaseHistoryConfig purchaseHistoryConfig;

    @Setter
    private PurchaseHistoryRepository historyRepository;

    @Getter
    @Setter
    private PurchaseHistory currentPurchaseHistory; // OrderInfo (to be refactored)

    public GeneralPurchaseHistoryCrawler(String site, WebpageService webpageService, ConfigurationRepository configurationRepository) {
        super(site, "purchase_history", webpageService, configurationRepository);
    }

    /**
     * fetch purchase history list
     *
     * @param webClient the web client
     * @param saveHtml  is need save html ?
     * @return result
     * @throws IOException if save html failed/parse json failed/get page failed
     */
    public GeneralPurchaseHistoryCrawlerResult fetchPurchaseHistoryList(TrafficWebClient webClient) throws IOException {
        LOGGER.debug("[fetchPurchaseHistoryList] in");

        webClient.getWebClient().getOptions().setJavaScriptEnabled(true);
        this.webClient = webClient;
        this.historyPage = new NavigablePurchaseHistoryPage(this.webClient);

        this.purchaseHistoryList = new LinkedList<>();
        this.savedPathList = new LinkedList<>();
        this.scrapedPageList = new ArrayList<>();
        this.scrapedOrderNumberList = new ArrayList<>();

        purchaseHistoryConfig = new ObjectMapper().readValue(this.jsonConfigText, PurchaseHistoryConfig.class);
        historyPage.setPage(purchaseHistoryConfig.getUrl());
        scrapedPageList.add(purchaseHistoryConfig.getUrl());

        try {
            processPurchaseHistory();
        } catch (DuplicatedException de) {
            LOGGER.info("Scraping duplicated Order Number detected.");
        }

        return new GeneralPurchaseHistoryCrawlerResult(this.purchaseHistoryList, this.savedPathList);
    }

    /**
     * get all purchase history
     *
     * @throws IOException if save html failed
     */
    private void processPurchaseHistory() throws IOException, DuplicatedException {
        LOGGER.debug("[processPurchaseHistory] in");

        while (this.historyPage.getPage() != null) {
            // if called from dryrun module check over maxcount or not.
            if (dryRunUtils != null && dryRunUtils.checkCountOver(purchaseHistoryList)) break;

            String savedPath = historyPage.savePage(this.site, "purchase-history-list", historyPage, this.webpageService);
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
    private void processOrders(List<DomNode> orderList, HtmlPage rootPage) throws IOException, DuplicatedException {
        LOGGER.debug("[processOrders] in");
        LOGGER.debug("[processOrders] Parsing page url " + historyPage.getPage().getUrl().toString());
        LOGGER.debug("[processOrders] Purchase list size =  " + orderList.size());

        PurchaseOrder orderConfig = purchaseHistoryConfig.getPurchaseOrder();

        Map<String, Integer> placeHolderNos = new HashMap<>();
        int i = 1;

        for (DomNode orderNode : orderList) {
            this.currentPurchaseHistory = new PurchaseHistory();
            this.historyPage.setPurchaseHistory(this.currentPurchaseHistory);
            placeHolderNos.put("orderIndex", i);
            scrapeOrder(rootPage, orderNode, orderConfig, currentPurchaseHistory, placeHolderNos);
            // skip process is database exist
            if (!isNew()) {
                LOGGER.debug(String.format("[processOrders] [%s] order %s already exist, skip this",
                        site, currentPurchaseHistory.getOrderNumber()));
                continue;
            }

            HtmlPage orderPage = rootPage;
            List<DomNode> productList;
            if (historyPage.isValid(orderConfig.getPurchaseProduct().getUrlElement())) {
                HtmlAnchor anchor = orderNode.querySelector(orderConfig.getPurchaseProduct().getUrlElement());
                orderPage = anchor.click();
                // fetch product by page root
                productList = orderPage.querySelectorAll(orderConfig.getPurchaseProduct().getParent());
                String savedPath = this.historyPage.savePage(this.site, "purchase-history-detail", orderPage, webpageService);
                if (savedPath != null) {
                    savedPathList.add(savedPath);
                }
            } else {
                // fetch product by order node
                productList = orderNode.querySelectorAll(orderConfig.getPurchaseProduct().getParent());
            }

            LOGGER.debug("[processOrders] productList.size() = " + productList.size());

            // On the contrast if a field for purchase_product appears under purchase_order, please reuse and set the scraped value to purchase_product
            ProductInfo reuseProduct = scrapeProduct(orderPage, orderNode, orderConfig, placeHolderNos);
            processProducts(productList, orderPage, reuseProduct, placeHolderNos);
            this.purchaseHistoryList.add(this.currentPurchaseHistory);
            i++;
        }
        LOGGER.info("[processOrders] done, size = " + this.purchaseHistoryList.size());
    }


    /**
     * scrape order
     *
     * @param urlElementPage the root page
     * @param orderNode      the order node
     * @param config         the selector config
     * @param history        the history item
     */
    private void scrapeOrder(HtmlPage urlElementPage, DomNode orderNode, PurchaseCommon config, PurchaseHistory history, Map<String, Integer> placeHolderNos) throws DuplicatedException {
        if (config.getOrderNumber() != null) {
            String orderNumber = historyPage.scrapeString(urlElementPage, orderNode, config.getOrderNumber(), placeHolderNos);
            if (scrapedOrderNumberList.contains(orderNumber)) throw new DuplicatedException();

            history.setOrderNumber(orderNumber);
            scrapedOrderNumberList.add(orderNumber);
        }
        if (config.getOrderDate() != null) {
            history.setOrderDate(historyPage.scrapeDate(urlElementPage, orderNode, config.getOrderDate(), placeHolderNos));
        }
        if (config.getTotalAmount() != null) {
            Float totalAmount = historyPage.scrapeFloat(urlElementPage, orderNode, config.getTotalAmount(), placeHolderNos);
            history.setTotalAmount(totalAmount == null ? null : totalAmount.toString());
        }
        if (config.getDeliveryStatus() != null) {
            history.setDeliveryStatus(historyPage.scrapeString(urlElementPage, orderNode, config.getDeliveryStatus(), placeHolderNos));
        }
    }

    /**
     * scrape product
     *
     * @param orderPage   the page
     * @param productNode the product node
     * @param config      the product selector config
     * @return the product
     */
    private ProductInfo scrapeProduct(HtmlPage orderPage, DomNode productNode, PurchaseCommon config, Map<String, Integer> placeHolderNos) {
        ProductInfo info = new ProductInfo();
        if (config.getProductCode() != null) {
            info.setCode(historyPage.scrapeString(orderPage, productNode, config.getProductCode(), placeHolderNos));
        }
        if (config.getProductName() != null) {
            info.setName(historyPage.scrapeString(orderPage, productNode, config.getProductName(), placeHolderNos));
        }
        if (config.getProductQuantity() != null) {
            Float quantity = historyPage.scrapeFloat(orderPage, productNode, config.getProductQuantity(), placeHolderNos);
            info.setQuantity(quantity == null ? null : quantity.intValue());
        }
        if (config.getUnitPrice() != null) {
            Float price = historyPage.scrapeFloat(orderPage, productNode, config.getUnitPrice(), placeHolderNos);
            info.setPrice(price == null ? null : price.toString());
        }
        if (config.getProductDistributor() != null) {
            info.setDistributor(historyPage.scrapeString(orderPage, productNode, config.getProductDistributor(), placeHolderNos));
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
    private void processProducts(List<DomNode> productList, HtmlPage orderPage, ProductInfo reuseProduct, Map<String, Integer> placeHolderNos) throws IOException, DuplicatedException {
        LOGGER.debug("[processProducts] in");

        PurchaseProduct productConfig = purchaseHistoryConfig.getPurchaseOrder().getPurchaseProduct();
        int i = 1;
        for (DomNode productNode : productList) {
            placeHolderNos.put("productIndex", i);
            this.currentProduct = scrapeProduct(orderPage, productNode, productConfig, placeHolderNos);
            BeanUtils.copyProperties(reuseProduct, currentProduct, Common.getNullPropertyNames(reuseProduct));

            if (this.currentProduct.getName() != null) {
                this.currentPurchaseHistory.addProduct(this.currentProduct);
            }
            //if a field for purchase_order appears under purchase_product, please set the scraped value to purchase_order.
            scrapeOrder(orderPage, productNode, productConfig, currentPurchaseHistory, placeHolderNos);
            i++;
        }
    }

    /**
     * check is in database or not
     *
     * @return result
     */
    protected boolean isNew() {
        return historyRepository == null
                || historyRepository.getByEcSiteAndOrderNo(site, currentPurchaseHistory.getOrderNumber()) == null;
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
        if (nextPageAnchor != null && !scrapedPageList.contains(nextPageAnchor.getHrefAttribute())) {
            LOGGER.info("[gotoNextPage] goto Next Page");
            scrapedPageList.add(nextPageAnchor.getHrefAttribute());
            return nextPageAnchor.click();
        }
        return null;
    }
}
