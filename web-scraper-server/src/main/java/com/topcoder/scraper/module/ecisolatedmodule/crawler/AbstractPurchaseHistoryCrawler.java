package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.lib.navpage.NavigablePurchaseHistoryPage;
import com.topcoder.scraper.service.WebpageService;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractPurchaseHistoryCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPurchaseHistoryCrawler.class);

  protected final Binding               configBinding;
  protected final CompilerConfiguration compConfig;
  protected GroovyShell                 scriptShell;
  protected String                      configText = "";

  protected PurchaseHistory       lastPurchaseHistory;
  protected List<String>          savedPathList;
  protected boolean               saveHtml;

  @Getter@Setter protected NavigablePurchaseHistoryPage historyPage;
  @Getter@Setter protected TrafficWebClient webClient;
  @Getter@Setter protected String           siteName;
  @Getter@Setter protected WebpageService   webpageService;

  @Getter@Setter protected PurchaseHistory       currentPurchaseHistory; // OrderInfo (to be refactored)
  @Getter@Setter protected ProductInfo           currentProduct;
  @Getter@Setter protected List<PurchaseHistory> purchaseHistoryList;

  public AbstractPurchaseHistoryCrawler(String siteName, WebpageService webpageService) {
    LOGGER.debug("[constructor] in");

    this.siteName       = siteName;
    this.webpageService = webpageService;

    String configPath = this.getConfigPath();
    this.configText = this.getConfigText(configPath);

    Properties configProps = new Properties();
    configProps.setProperty("groovy.script.base", this.getScriptSupportClassName());
    this.compConfig = new CompilerConfiguration(configProps);
    this.configBinding = new Binding();
  }

  protected String getConfigPath() {
    LOGGER.debug("[getConfigPath] in");

    String configPath = System.getenv(Consts.SCRAPING_SCRIPT_PATH);
    if (StringUtils.isEmpty(configPath)) {
      configPath = System.getProperty("user.dir") + "/scripts/scraping";
    }
    configPath  += "/isolated/" + this.siteName + "-purchase-history-list.groovy";

    LOGGER.debug("[getConfigPath] configPath: " + configPath);
    return configPath;
  }

  protected String getConfigText(String configPath) {
    LOGGER.debug("[getConfigText] in");

    try {
      return FileUtils.readFileToString(new File(configPath), "utf-8");
    } catch (IOException e) {
      LOGGER.debug("[getConfigText] Could not read config file: " + configPath);
      return null;
    }
  }

  protected abstract String getScriptSupportClassName();

  protected String executeConfig() {
    LOGGER.debug("[executeConfig] in");
    this.scriptShell = new GroovyShell(this.configBinding, this.compConfig);
    Script script = this.scriptShell.parse(this.configText);
    script.invokeMethod("setCrawler", this);
    String resStr = (String)script.run();
    return resStr;
  }

  public AbstractPurchaseHistoryCrawlerResult fetchPurchaseHistoryList(TrafficWebClient webClient, PurchaseHistory lastPurchaseHistory, boolean saveHtml) throws IOException {
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

    return new AbstractPurchaseHistoryCrawlerResult(this.purchaseHistoryList, this.savedPathList);
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
    LOGGER.debug("Parsing page url " + historyPage.getPage().getUrl().toString());

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

  protected HtmlPage gotoNextPage(HtmlPage page, TrafficWebClient webClient) throws IOException {
    LOGGER.debug("[gotoNextPage] in");
    return null;
  }
}
