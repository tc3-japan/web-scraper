package com.topcoder.scraper.module.ecisolatedmodule.crawler;

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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public abstract class AbstractPurchaseHistoryListCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPurchaseHistoryListCrawler.class);

  protected final Binding               scriptBinding;
  protected final CompilerConfiguration scriptConfig;
  protected GroovyShell                 scriptShell;
  protected String                      scriptText = "";

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

  public AbstractPurchaseHistoryListCrawler(String siteName, WebpageService webpageService) {
    LOGGER.info("[constructor] in");

    this.siteName       = siteName;
    this.webpageService = webpageService;

    String scriptPath = this.getScriptPath();
    this.scriptText   = this.getScriptText(scriptPath);

    Properties configProps = new Properties();
    configProps.setProperty("groovy.script.base", this.getScriptSupportClassName());
    this.scriptConfig  = new CompilerConfiguration(configProps);
    this.scriptBinding = new Binding();
  }

  protected String getScriptPath() {
    LOGGER.info("[getScriptPath] in");

    String scriptPath = System.getenv(Consts.SCRAPING_SCRIPT_PATH);
    if (StringUtils.isEmpty(scriptPath)) {
      scriptPath = System.getProperty("user.dir") + "/scripts/scraping";
    }
    scriptPath  += "/isolated/" + this.siteName + "-purchase-history-list.groovy";

    LOGGER.info("[getScriptPath] scriptPath: " + scriptPath);
    return scriptPath;
  }

  protected String getScriptText(String scriptPath) {
    LOGGER.info("[getScriptText] in");

    try {
      return FileUtils.readFileToString(new File(scriptPath), "utf-8");
    } catch (IOException e) {
      LOGGER.info("[getScriptText] Could not read script file: " + scriptPath);
      return null;
    }
  }

  protected abstract String getScriptSupportClassName();

  protected String executeScript() {
    LOGGER.info("[executeScript] in");
    this.scriptShell = new GroovyShell(this.scriptBinding, this.scriptConfig);
    Script script = this.scriptShell.parse(this.scriptText);
    script.invokeMethod("setCrawler", this);
    String resStr = (String)script.run();
    return resStr;
  }

  public AbstractPurchaseHistoryListCrawlerResult fetchPurchaseHistoryList(TrafficWebClient webClient, PurchaseHistory lastPurchaseHistory, boolean saveHtml) throws IOException {
    LOGGER.info("[fetchPurchaseHistoryList] in");

    this.webClient = webClient;
    this.saveHtml  = saveHtml;
    this.historyPage = new NavigablePurchaseHistoryPage(this.webClient);

    this.lastPurchaseHistory = lastPurchaseHistory;
    this.purchaseHistoryList = new LinkedList<>();
    this.savedPathList       = new LinkedList<>();

    // binding variables for scraping script
    // TODO: re-consider whether this is necessary
    this.scriptBinding.setProperty("purchaseHistoryList", this.purchaseHistoryList);

    this.executeScript();

    return new AbstractPurchaseHistoryListCrawlerResult(this.purchaseHistoryList, this.savedPathList);
  }

  // TODO: re-consider Closure<HERE>, now temporarily Boolean
  public void processPurchaseHistory(Closure<Boolean> closure) throws IOException {
    LOGGER.info("[processPurchaseHistory] in");

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
    LOGGER.info("[processOrders] in");
    LOGGER.debug("Parsing page url " + historyPage.getPage().getUrl().toString());

    for (DomNode orderNode : orderList) {
      this.currentPurchaseHistory = new PurchaseHistory();
      this.historyPage.setPurchaseHistory(this.currentPurchaseHistory);

      closure.call(orderNode);

      this.purchaseHistoryList.add(this.currentPurchaseHistory);
    }
  }

  public void processProducts(List<DomNode> productList, Closure<Boolean> closure) {
    LOGGER.info("[processProducts] in");

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
    LOGGER.info("[isNew] in");
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
    LOGGER.info("[gotoNextPage] in");
    return null;
  }
}
