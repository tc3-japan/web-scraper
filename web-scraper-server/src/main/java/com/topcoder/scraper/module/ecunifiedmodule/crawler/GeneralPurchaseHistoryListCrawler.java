package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import static com.topcoder.common.util.HtmlUtils.extract;
import static com.topcoder.common.util.HtmlUtils.extractInt;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.DateUtils;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.service.WebpageService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class GeneralPurchaseHistoryListCrawler extends GeneralPurchaseHistoryListCrawlerScriptSupport {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralPurchaseHistoryListCrawler.class);
  private static final Pattern PAT_ORDER_NO = Pattern.compile("([\\d]{13})", Pattern.DOTALL);
  protected final String siteName;
  protected final WebpageService webpageService;
  public TrafficWebClient webClient;

  // variables
  private final Binding               scriptBinding;
  private final CompilerConfiguration scriptConfig;
  private GroovyShell                 scriptShell;
  private String                      scriptText = "";
  
  public GeneralPurchaseHistoryListCrawler(String siteName, WebpageService webpageService) {
    this.siteName = siteName;
    this.webpageService = webpageService;

    GeneralPurchaseHistoryListCrawlerScriptSupport.setCrawler(this); //////////??????
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

  private String executeScript() {
    LOGGER.info("[executeScript] in");
    this.scriptShell = new GroovyShell(this.scriptBinding, this.scriptConfig);
    Script script = scriptShell.parse(this.scriptText);
    String resStr = (String)script.run();
    return resStr;
  }

  public GeneralPurchaseHistoryListCrawlerResult fetchPurchaseHistoryList(TrafficWebClient webClient, PurchaseHistory lastPurchaseHistory, boolean saveHtml) throws IOException {
    List<PurchaseHistory> list = new LinkedList<>();
    List<String> pathList = new LinkedList<>();
    LOGGER.info("goto Order History Page");
    this.webClient = webClient;
    /*
    HtmlPage page = webClient.getPage("https://www.kojima.net/ec/member/CMmOrderHistory.jsp");
    if (page.getBaseURI().contains("?autoLogin")) {
      throw new SessionExpiredException("Session has been expired.");      
    }
    
    webpageService.save("kojima-purchase-history", siteName, page.getWebResponse().getContentAsString());
    while (true) {
      if (page == null || !parsePurchaseHistory(list, page, lastPurchaseHistory, saveHtml, pathList)) {
        break;
      }
      page = gotoNextPage(page, webClient);
    }
    */

    this.executeScript();

    return new GeneralPurchaseHistoryListCrawlerResult(list, pathList);
  }
  
  private boolean parsePurchaseHistory(List<PurchaseHistory> list, HtmlPage page, PurchaseHistory last, boolean saveHtml, List<String> pathList) {

    LOGGER.debug("Parsing page url " + page.getUrl().toString());
    
    List<DomNode> orders = page.querySelectorAll(".member-orderhistorydetails > tbody");

    for (DomNode orderNode : orders) {
      DomNode itemNoNode = orderNode.querySelector(".itemnumber");
      String nodeText = itemNoNode.asText();
      String orderNumber = extract(nodeText, PAT_ORDER_NO);
      LOGGER.info("ORDER NO: " + orderNumber);

      Date orderDate = extractDate(nodeText);
      LOGGER.info("ORDER DATE: " + orderDate);
      
      DomNode itemTotalAmountNode = orderNode.querySelector(".totalamountmoney");
      Integer totalAmount = itemTotalAmountNode != null ? extractInt(itemTotalAmountNode.asText()) : null;
      LOGGER.info("TOTAL: " + totalAmount);
            
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
    /*

    List<DomNode> orders = page.querySelectorAll(".member-orderhistorydetails > tbody");
    for (DomNode orderNode : orders) {
      DomNode itemNoNode = orderNode.querySelector(".itemnumber");
      String nodeText = itemNoNode.asText();
      String orderNumber = extract(nodeText, PAT_ORDER_NO);
      LOGGER.info("ORDER NO: " + orderNumber);

      Date orderDate = extractDate(nodeText);
      LOGGER.info("ORDER DATE: " + orderDate);
      
      DomNode itemTotalAmountNode = orderNode.querySelector(".totalamountmoney");
      Integer totalAmount = itemTotalAmountNode != null ? extractInt(itemTotalAmountNode.asText()) : null;
      LOGGER.info("TOTAL: " + totalAmount);
            
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
    */
    return false;
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

  @Override
  public Object run() {
    // TODO Auto-generated method stub
    return null;
  }

}
