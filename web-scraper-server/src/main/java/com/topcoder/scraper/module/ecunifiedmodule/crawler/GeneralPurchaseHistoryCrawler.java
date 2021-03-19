package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import java.io.IOException;
import java.util.*;
import java.time.LocalDate;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.topcoder.api.service.login.LoginHandler;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.LoginResponse;
import com.topcoder.scraper.exception.CheckLoginException;
import com.topcoder.scraper.exception.NotLoggedinException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.model.scraper.*;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.repository.PurchaseHistoryRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.Common;
import com.topcoder.scraper.lib.navpage.NavigablePurchaseHistoryPage;
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
    private ECSiteAccountDAO ecSiteAccountDAO;

    @Getter
    @Setter
    private PurchaseHistory currentPurchaseHistory; // OrderInfo (to be refactored)

    private boolean isChangeDetection = false;

    private LoginHandler loginHandler = null;

    private Integer maxCountForDetection = null;

    public GeneralPurchaseHistoryCrawler
            (String site,
             WebpageService webpageService,
             ConfigurationRepository configurationRepository,
             PurchaseHistoryRepository historyRepository,
             LoginHandler loginHandler,
             ECSiteAccountDAO ecSiteAccountDAO,
             Integer maxCountForDetection) {

        super(site, "purchase_history", webpageService, configurationRepository);
        this.historyRepository = historyRepository;
        this.maxCountForDetection = maxCountForDetection;
        this.loginHandler = loginHandler;
        this.ecSiteAccountDAO = ecSiteAccountDAO;
        this.isChangeDetection = maxCountForDetection != null;
    }

    public GeneralPurchaseHistoryCrawler(String site, WebpageService webpageService, ConfigurationRepository configurationRepository) {
        super(site, "purchase_history", webpageService, configurationRepository);
    }

    /**
     * fetch purchase history list
     *
     * @param webClient the web client
     * @return result
     * @throws IOException if save html failed/parse json failed/get page failed
     */
    public GeneralPurchaseHistoryCrawlerResult fetchPurchaseHistoryList(TrafficWebClient webClient) throws IOException, NotLoggedinException {
        LOGGER.debug("[fetchPurchaseHistoryList] in");

        webClient.getWebClient().getOptions().setJavaScriptEnabled(true);
        this.webClient = webClient;
        this.historyPage = new NavigablePurchaseHistoryPage(this.webClient);

        this.purchaseHistoryList = new LinkedList<>();
        this.savedPathList = new LinkedList<>();
        this.scrapedPageList = new ArrayList<>();
        this.scrapedOrderNumberList = new ArrayList<>();

        purchaseHistoryConfig = new ObjectMapper().readValue(this.jsonConfigText, PurchaseHistoryConfig.class);

        try {
            processPurchaseHistory();
        } catch (DuplicatedException de) {
            LOGGER.info("Scraping duplicated Order Number detected.");
        }

        return new GeneralPurchaseHistoryCrawlerResult(this.purchaseHistoryList, this.savedPathList);
    }

    /**
     * @throws IOException          if save html failed
     * @throws DuplicatedException  if duplicated
     * @throws NotLoggedinException if logged in failed
     */
    private void processPurchaseHistory() throws IOException, DuplicatedException, NotLoggedinException {
        if (purchaseHistoryConfig.getUrl().endsWith("{year}")) {
            List<Integer> years = getYearFromOrderFilter();
            for (Integer year : years) {
                if (isMaxCount()) break;

                String url = purchaseHistoryConfig.getUrl().replace("{year}", String.valueOf(year));
                processPurchaseHistory(url);
            }
        } else {
            processPurchaseHistory(purchaseHistoryConfig.getUrl());
        }
    }

    /**
     * get purchase history year from order filter.
     *
     * @return purchase history year list
     */
    private List<Integer> getYearFromOrderFilter() throws IOException {
        LOGGER.debug("[getYearFromOrderFilter] in");
        List<Integer> years = new LinkedList<>();

        int currentYear = LocalDate.now().getYear();
        String url = purchaseHistoryConfig.getUrl().replace("{year}", String.valueOf(currentYear));

        try {
            historyPage.setPage(url);
        } catch(FailingHttpStatusCodeException e) {
            LOGGER.info(e.getMessage());
            tryLogin(url);
        }

        if (this.historyPage.getPage() != null) {
            if (isRedirected()) {
                tryLogin(url);
            }

            Selector orderFilter = purchaseHistoryConfig.getOrderFilter();
            List<DomNode> orderFilterList = historyPage.getPage().querySelectorAll(orderFilter.getElement());

            for (DomNode filterNode : orderFilterList) {
                String yearStr = historyPage.scrapeStringFromNode(filterNode, orderFilter);
                if (yearStr != null && !yearStr.isEmpty()) {
                    years.add(Integer.valueOf(yearStr));
                }
            }
        }
        LOGGER.debug("[getYearFromOrderFilter] done, size = " + years.size());
        return years;
    }

    /**
     * get all purchase history
     *
     * @param url the transition destination
     * @throws IOException          if save html failed
     * @throws DuplicatedException  if duplicated
     * @throws NotLoggedinException if logged in failed
     */
    private int processPurchaseHistory(String url) throws IOException, DuplicatedException, NotLoggedinException {
        LOGGER.debug("[processPurchaseHistory] in");

        // Rakuten dummy Cookies to test
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.DATE, 1);
//        Date date = calendar.getTime();
//
//        CookieManager cookieManager = webClient.getWebClient().getCookieManager();
//        cookieManager.clearCookies();
//        cookieManager.addCookie(new Cookie(".rakuten.co.jp", "Rq", "60121ce9", "/", date, true));
//        cookieManager.addCookie(new Cookie(".rakuten.co.jp", "Rz", "A1y0UVjdn0f0cbv-fJexxmL8UpG8x2xt9pj7mkhcUj4ylwpvM2tDzyTG94guBi9nB5cx9-sI_99MbFy6q94_4J-60u0sv7oGzm7nKnFgbRRjGE6qR-5snKA2yJp0xQ_Ru8dHOW6gn6ybWesK5e7FuR4~", "/", date, true));
//        cookieManager.addCookie(new Cookie(".rd.rakuten.co.jp", "Rp_s", "fb4ef25e.5b9df49a6255e", "/", date, false));
//        cookieManager.addCookie(new Cookie(".rakuten.co.jp", "_ra", "1611743534888|37e03bfc-5778-4e5a-9fb6-4db862d532c3", "/", date, true));
//        cookieManager.addCookie(new Cookie(".rakuten.co.jp", "Rb", "1s042c0s08", "/", date, true));
//        cookieManager.addCookie(new Cookie(".rakuten.co.jp", "Ra", "Aw0uIfhlAuFfN8CgmjPaTR2Bc9CXMgQXU0GGB9nFmivkFFLT8tJRWKSi0lw0hrXg3LdZ0I7-d-kyiWt6XaTbaFZKKDKFXwUw6a1Aj-1wOqvPBwq4WbBNL3sBKgYA8Jo0XPY~", "/", date, true));
//        cookieManager.addCookie(new Cookie(".rakuten.co.jp", "Ry", "1&5b857bd064dbdad4fc424b51efb0585d8d8427d40ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEF&20210128110945", "/", date, true));
//        cookieManager.addCookie(new Cookie(".rakuten.co.jp", "Rp", "dc6b12127c270d068233e7be386011412ef0167", "/", date, true));

        try {
            historyPage.setPage(url);
        } catch (FailingHttpStatusCodeException e) {
            LOGGER.info(e.getMessage());
            e.printStackTrace();
            webClient.finishTraffic();
            tryLogin(url);
        }

        scrapedPageList.add(url);

        int orderListSize = 0;
        while (this.historyPage.getPage() != null) {
            if (isRedirected()) {
                tryLogin(url);
            }

            // if called from dryrun module check over maxcount or not.
            if (isMaxCount()) break;

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
            orderListSize = orderList.size();
            processOrders(orderList, rootPage);
            this.historyPage.setPage(this.gotoNextPage(this.historyPage.getPage(), webClient));
        }
        return orderListSize;
    }

    private boolean tryLogin(String url) throws NotLoggedinException, IOException {
        if (!"rakuten".equals(this.site)) {
            throw new NotLoggedinException("Login check failed due to redirection, url:" + url);
        }

        LoginResponse loginResponse = loginHandler.login(ecSiteAccountDAO);
        if (loginResponse == null) {
            throw new NotLoggedinException("Login retry failed.");
        }

        boolean restoreRet = Common.restoreCookies(webClient.getWebClient(), ecSiteAccountDAO);
        if (!restoreRet) {
            String message = "skip ec site account id = " + ecSiteAccountDAO.getId() + ", restore cookies failed";
            Common.ZabbixLog(LOGGER, message);
            throw new NotLoggedinException(message);
        }

        historyPage.setPage(url);
        return true;
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
            currentPurchaseHistory.setAccountId(Integer.toString(ecSiteAccountDAO.getId()));
            // skip process is database exist
            if (!isNew()) {
                LOGGER.debug(String.format("[processOrders] [%s] order %s already exist, skip this",
                        site, currentPurchaseHistory.getOrderNumber()));
                throw new DuplicatedException();
            }

            HtmlPage orderPage = rootPage;
            List<DomNode> productList;
            if (historyPage.isValid(orderConfig.getPurchaseProduct().getUrlElement())) {
                HtmlAnchor anchor = orderNode.querySelector(orderConfig.getPurchaseProduct().getUrlElement());
                orderPage = webClient.click(anchor);
                if (!orderPage.getUrl().toString().equals(anchor.getHrefAttribute())) {
                    LOGGER.debug("[processOrders] anchor.click() failed to URL = " + anchor.getHrefAttribute());
                    orderPage = webClient.getPage(anchor.getHrefAttribute());
                    LOGGER.debug("[processOrders] URL after getting href page = " + orderPage.getUrl().toString());
                }
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

            if (isMaxCount()) break;
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
        if (isChangeDetection) return true;
        return historyRepository == null
                || historyRepository.getByEcSiteAndAccountIdAndOrderNo
                (site, currentPurchaseHistory.getAccountId(), currentPurchaseHistory.getOrderNumber()).isEmpty();
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

    /**
     * fetch purchase history list for login check
     *
     * @param webClient the web client
     * @throws IOException         if save html failed/parse json failed/get page failed
     * @throws CheckLoginException if response http status code 302
     */
    public void goToPurchaseHistoryListForLoginCheck(TrafficWebClient webClient) throws IOException, CheckLoginException {
        LOGGER.debug("[goToPurchaseHistoryListForLoginCheck] in");

        webClient.getWebClient().getOptions().setJavaScriptEnabled(true);
        this.webClient = webClient;
        this.historyPage = new NavigablePurchaseHistoryPage(this.webClient);

        purchaseHistoryConfig = new ObjectMapper().readValue(this.jsonConfigText, PurchaseHistoryConfig.class);

        try {
            historyPage.setPage(purchaseHistoryConfig.getUrl());
        } catch(FailingHttpStatusCodeException e) {
            LOGGER.info(e.getMessage());
            throw new CheckLoginException("Login check failed due to redirection, url:" + purchaseHistoryConfig.getUrl());
        }

        if (isRedirected()) {
            throw new CheckLoginException("Login check failed due to redirection, url:" + purchaseHistoryConfig.getUrl());
        }
    }

    /**
     * Determine if the URL was redirected.
     *
     * @return boolean isRedirected
     */
    private boolean isRedirected() {
        String domain = historyPage.getPageUrl().toString().split("\\?")[0];
        String configDomain = purchaseHistoryConfig.getUrl().split("\\?")[0];

        if (domain.contains(configDomain)) return false;

        LOGGER.debug("Page URL:{}, Config URL:{}", domain, configDomain);
        return true;
    }

    private boolean isMaxCount() {
        if (dryRunUtils != null && dryRunUtils.checkCountOver(purchaseHistoryList)) return true;
        if (isChangeDetection && maxCountForDetection > 0 &&
                purchaseHistoryList.size() >= maxCountForDetection) return true;
        return false;
    }
}
