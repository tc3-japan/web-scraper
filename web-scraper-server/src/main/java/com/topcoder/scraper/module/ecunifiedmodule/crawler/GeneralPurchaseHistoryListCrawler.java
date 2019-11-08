package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import static com.topcoder.common.util.HtmlUtils.extract;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.DateUtils;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.lib.navpage.NavigablePurchaseHistoryPage;
import com.topcoder.scraper.service.WebpageService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;
import lombok.Setter;

public class GeneralPurchaseHistoryListCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPurchaseHistoryListCrawler.class);
  private static final Pattern PAT_ORDER_NO = Pattern.compile("([\\d]{13})", Pattern.DOTALL);
  protected final String siteName;
  protected final WebpageService webpageService;
  public TrafficWebClient webClient;
  protected List<String>          savedPathList;

  // variables
  private final Binding               scriptBinding;
  private final CompilerConfiguration scriptConfig;
  private GroovyShell                 scriptShell;
  private String                      scriptText = "";
  protected PurchaseHistory       lastPurchaseHistory;
  protected boolean               saveHtml;
  @Getter@Setter protected NavigablePurchaseHistoryPage historyPage;
  @Getter@Setter protected PurchaseHistory       currentPurchaseHistory; // OrderInfo (to be refactored)
  @Getter@Setter protected ProductInfo           currentProduct;
  @Getter@Setter protected List<PurchaseHistory> purchaseHistoryList;
  
  public GeneralPurchaseHistoryListCrawler(String siteName, WebpageService webpageService) {
    this.siteName = siteName;
    this.webpageService = webpageService;

    GeneralPurchaseHistoryListCrawlerScriptSupport.setCrawler(this);
    
    String scriptPath = this.getScriptPath();
    this.scriptText   = this.getScriptText(scriptPath);

    Properties configProps = new Properties();
    configProps.setProperty("groovy.script.base", GeneralPurchaseHistoryListCrawlerScriptSupport.class.getName());
    this.scriptConfig  = new org.codehaus.groovy.control.CompilerConfiguration(configProps);
    this.scriptBinding = new Binding();
  }

  private String getScriptPath() {
    LOGGER.info("[getScriptPath] in");

    String scriptPath = System.getenv(Consts.SCRAPING_SCRIPT_PATH);
    if (StringUtils.isEmpty(scriptPath)) {
      scriptPath = System.getProperty("user.dir") + "/scripts/scraping";
    }
    scriptPath  += "/" + this.siteName + "-purchase-history.groovy";

    LOGGER.info("scriptPath: " + scriptPath);
    return scriptPath;
  }

  private String getScriptText(String scriptPath) {
    LOGGER.info("[getScriptText] in");

    try {
      return FileUtils.readFileToString(new File(scriptPath), "utf-8");
    } catch (IOException e) {
      LOGGER.info("Could not read script file: " + scriptPath);
      return null;
    }
  }

  //protected abstract String getScriptGeneralPurchaseHistoryListCrawlerScriptSupport.class.getName();

  private String executeScript() {
    LOGGER.info("[executeScript] in");
    this.scriptShell = new GroovyShell(this.scriptBinding, this.scriptConfig);
    Script script = scriptShell.parse(this.scriptText);
    script.invokeMethod("setCrawler", this);
    String resStr = (String)script.run();
    return resStr;
  }

  public GeneralPurchaseHistoryListCrawlerResult fetchPurchaseHistoryList(TrafficWebClient webClient, PurchaseHistory lastPurchaseHistory, boolean saveHtml) throws IOException {
    //List<PurchaseHistory> list = new LinkedList<>();
    //List<String> pathList = new LinkedList<>();
    LOGGER.info("goto Order History Page");
    
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

    GeneralPurchaseHistoryListCrawlerResult result = new GeneralPurchaseHistoryListCrawlerResult(this.purchaseHistoryList, this.savedPathList);
    //GeneralPurchaseHistoryListCrawlerResult resultOriginal = new GeneralPurchaseHistoryListCrawlerResult(list, pathList);
    return result;
  }
  

  // TODO: re-consider Closure<HERE>, now temporarily Boolean
  public GeneralPurchaseHistoryListCrawlerResult processPurchaseHistory(Closure<Boolean> closure) throws IOException {
    LOGGER.info("[processPurchaseHistory] in");

    System.out.println("historyPage: " + historyPage);
    /* THIS IS BUGGED?
    while (true) {
      if (this.historyPage.getPage() == null) {
        System.out.println("historyPage is null!");
        break;
      }
      closure.call();
      this.historyPage.setPage(this.gotoNextPage(this.historyPage.getPage(), webClient));
    }
    */
    closure.call();
    return new GeneralPurchaseHistoryListCrawlerResult(this.purchaseHistoryList, this.savedPathList);
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

  protected boolean isNew(PurchaseHistory purchase, PurchaseHistory last) {
    Date orderDate = purchase.getOrderDate();
    Date lastOrderDate = last != null ? last.getOrderDate() : null;
    String orderNumber = purchase.getOrderNumber();
    String lastOrderNo = last != null ? (last.getOrderNumber() != null ? last.getOrderNumber() : "") : "";

    if ((orderDate != null && lastOrderDate != null && orderDate.compareTo(lastOrderDate) <= 0) || lastOrderNo.equals(orderNumber)) {
      return false;
    }
    return true;
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
  
  
  private HtmlPage gotoNextPage(HtmlPage page, TrafficWebClient webClient) throws IOException {
    return null;
  }
  
  
  private static final Pattern PAT_DATE = Pattern.compile("(20[\\d]{2}/[\\d]{2}/[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2})", Pattern.DOTALL);
  private static final String FORMAT_DATE = "yyyy/MM/dd HH:mm:ss";
  
  protected String normalizeProductName(String productName) {
    if (productName == null) {
      return productName;
    }
    return productName.trim().replaceAll("　", " ");
  }
  
  protected Date extractDate(String text) {
    String dateStr = extract(text, PAT_DATE);
    try {
      return DateUtils.fromString(dateStr, FORMAT_DATE);
    } catch (ParseException e) {
      LOGGER.error(String.format("Failed to parse the input '%s'. Error: %s", dateStr, e.getMessage()));
      e.printStackTrace();
      return null;
    }
  }
  
	protected String getText(HtmlElement element, String selector) {
		if (element != null) {
			DomNode node = element.querySelector(selector);
			String str = node != null ? node.asText().replaceAll("\\n", " ").trim() : null;
			return str;
		} else {
			LOGGER.error("productDetailPage is null at ProductDetailCrawler.java > getText()");
			return null;
		}
  }
  

	protected void setDistributor(HtmlElement element, ProductInfo productInfo, String selector) {
		String str = getText(element, selector);
		if(str != null) {
			productInfo.setDistributor(str);
		}
	}

	protected void setCode(HtmlElement element, ProductInfo productInfo, String selector) {
		String code = getText(element, selector);
		if(code != null) {
			productInfo.setCode(code);
		}
	}
	protected void setName(HtmlElement element, ProductInfo productInfo, String selector) {
		String str = getText(element, selector);
		if(str != null) {
			productInfo.setName(str);
		}
	}
	protected void setPrice(HtmlElement element, ProductInfo productInfo, String selector) {
		String str = getText(element, selector);
		if(str != null) {
			productInfo.setPrice(str);
		}
	}
	protected void setModelNo(HtmlElement element, ProductInfo productInfo, String selector) {
		String str = getText(element, selector);
		str = str.replaceAll("[^0-9a-zA-Z\\-]", "").trim();
		if(str != null) {
			productInfo.setModelNo(str);
		}
  }

  //@Override
  public Object run() {
    // TODO Auto-generated method stub
    return null;
  }

}
