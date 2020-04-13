package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.codehaus.groovy.control.CompilerConfiguration;
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

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;
import lombok.Setter;

public class GeneralPurchaseHistoryCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPurchaseHistoryCrawler.class);

  private final Binding               configBinding;
  private final CompilerConfiguration compConfig;
  private GroovyShell                 scriptShell;
  private String                      configText = "";

  private PurchaseHistory       lastPurchaseHistory;
  private List<String>          savedPathList;
  private boolean               saveHtml;

  @Getter@Setter private NavigablePurchaseHistoryPage historyPage;
  @Getter@Setter private TrafficWebClient webClient;
  @Getter@Setter private String           siteName;
  @Getter@Setter private WebpageService   webpageService;

  @Getter@Setter private PurchaseHistory       currentPurchaseHistory; // OrderInfo (to be refactored)
  @Getter@Setter private ProductInfo           currentProduct;
  @Getter@Setter private List<PurchaseHistory> purchaseHistoryList;

  public GeneralPurchaseHistoryCrawler(String siteName, WebpageService webpageService, ConfigurationRepository configurationRepository) {
    LOGGER.debug("[constructor] in");
    this.siteName = siteName;
    this.webpageService = webpageService;
    this.configText = this.getConfigFromDB(siteName, "purchase_history", configurationRepository);
    Properties configProps = new Properties();
    configProps.setProperty("groovy.script.base", this.getScriptSupportClassName());
    this.compConfig = new CompilerConfiguration(configProps);
    this.configBinding = new Binding();
  }

  private String getConfigFromDB(String site, String type, ConfigurationRepository configurationRepository) {
	LOGGER.debug("[getConfigFromDB] in");
	LOGGER.debug("[getConfigFromDB] site:" + site + " type:" + type);
	ConfigurationDAO configurationDAO = configurationRepository.findBySiteAndType(site, type);
	return configurationDAO.getConfig();
  }

  public void setConfig(String conf) {
	LOGGER.debug("[setConfig] in");
	LOGGER.debug("conf = " + conf);
	if (conf != null && conf != "") {
      this.configText = conf;
    }
  }

  private String getScriptSupportClassName() {
    return GeneralPurchaseHistoryCrawlerScriptSupport.class.getName();
  }

  private String executeConfig() {
    LOGGER.debug("[executeConfig] in");
    this.scriptShell = new GroovyShell(this.configBinding, this.compConfig);
    Script script = scriptShell.parse(this.configText);
    script.invokeMethod("setCrawler", this);
    String resStr = (String)script.run();
    return resStr;
  }

  public GeneralPurchaseHistoryCrawlerResult fetchPurchaseHistoryList(TrafficWebClient webClient, PurchaseHistory lastPurchaseHistory, boolean saveHtml) throws IOException {
    LOGGER.debug("[fetchPurchaseHistoryList] in");

    this.webClient = webClient;
    this.saveHtml  = saveHtml;
    this.historyPage = new NavigablePurchaseHistoryPage(this.webClient);

    this.lastPurchaseHistory = lastPurchaseHistory;
    this.purchaseHistoryList = new LinkedList<>();
    this.savedPathList       = new LinkedList<>();

    // binding variables for scraping config
    // TODO: re-consider whether this is necessary
    this.configBinding.setProperty("purchaseHistoryList", this.purchaseHistoryList);

    this.executeConfig();

    return new GeneralPurchaseHistoryCrawlerResult(this.purchaseHistoryList, this.savedPathList);
  }

  // TODO: re-consider Closure<HERE>, now temporarily Boolean
  public void processPurchaseHistory(Closure<Boolean> closure) throws IOException {
    LOGGER.debug("[processPurchaseHistory] in");

    this.webpageService.save(this.siteName + "-purchase-history", this.siteName, this.historyPage.getPage().getWebResponse().getContentAsString(), this.saveHtml);
    // TODO : implement
    /*
    if (page.getBaseURI().contains("?autoLogin")) {
      throw new SessionExpiredException("Session has been expired.");
    }
     */

    while (true) {
      if (this.historyPage.getPage() == null) {
        break;
      }
      closure.call();
      this.historyPage.setPage(this.gotoNextPage(this.historyPage.getPage(), webClient));
    }
  }

  public void processOrders(List<DomNode> orderList, Closure<Boolean> closure) {
    LOGGER.debug("[processOrders] in");
    LOGGER.debug("[processOrders] Parsing page url " + historyPage.getPage().getUrl().toString());

    for (DomNode orderNode : orderList) {
      this.currentPurchaseHistory = new PurchaseHistory();
      this.historyPage.setPurchaseHistory(this.currentPurchaseHistory);

      closure.call(orderNode);

      this.purchaseHistoryList.add(this.currentPurchaseHistory);
    }
  }

  public void processProducts(List<DomNode> productList, Closure<Boolean> closure) {
    LOGGER.debug("[processProducts] in");

    for (DomNode productNode : productList) {
      this.currentProduct = new ProductInfo();
      this.historyPage.setProductInfo(this.currentProduct);

      closure.call(productNode);

      if (this.currentProduct.getName() != null) {
        this.currentPurchaseHistory.addProduct(this.currentProduct);
      }
    }
  }

  public boolean isNew() {
    LOGGER.debug("[isNew] in");
    PurchaseHistory curr = this.currentPurchaseHistory;
    PurchaseHistory last = this.lastPurchaseHistory;

    Date currOrderDate = curr.getOrderDate();
    Date lastOrderDate = last != null ? last.getOrderDate() : null;
    String currOrderNumber = curr.getOrderNumber();
    String lastOrderNumber = last != null ? (last.getOrderNumber() != null ? last.getOrderNumber() : "") : "";

    if ((currOrderDate != null && lastOrderDate != null && currOrderDate.compareTo(lastOrderDate) <= 0) || lastOrderNumber.equals(currOrderNumber)) {
      return false;
    }
    return true;
  }

  private HtmlPage gotoNextPage(HtmlPage page, TrafficWebClient webClient) throws IOException {
    LOGGER.debug("[gotoNextPage] in");
    return null;
  }
}
