package com.topcoder.scraper.lib.navpage;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLInputElement;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.DateUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static com.topcoder.common.util.HtmlUtils.extract;
import static com.topcoder.common.util.HtmlUtils.extract1;
import static com.topcoder.common.util.HtmlUtils.extractInt;
import static com.topcoder.common.util.HtmlUtils.getAnchorHref;
import static com.topcoder.common.util.HtmlUtils.getTextContent;

public class NavigablePurchaseHistoryPage extends NavigablePage {

  private static final Logger LOGGER = LoggerFactory.getLogger(NavigablePurchaseHistoryPage.class);

  // TrafficWebClient webClient;
  // HtmlPage page;
  @Getter@Setter private PurchaseHistory purchaseHistory;
  @Getter@Setter private ProductInfo     productInfo ;

  public NavigablePurchaseHistoryPage(HtmlPage page, TrafficWebClient webClient) {
    super(page, webClient);
    LOGGER.info("[constructor] in");
  }

  public NavigablePurchaseHistoryPage(String url, TrafficWebClient webClient) {
    super(url, webClient);
    LOGGER.info("[constructor] in");
  }

  public NavigablePurchaseHistoryPage(TrafficWebClient webClient) {
    super((HtmlPage)null, webClient);
    LOGGER.info("[constructor] in");
  }

  public void scrapeAccountId(String selector) {
    LOGGER.info("[scrapeAccountId] in");
    String str = getText(selector);
    //LOGGER.info(" >>> Setting Account ID >>>" + str);
    if (str != null) {
      purchaseHistory.setAccountId(str);
    }
  }

  public void scrapeAccountId(DomNode node, String selector) {
    LOGGER.info("[scrapeAccountId] in");
    String str = getText(node, selector);
    //LOGGER.info(" >>> Setting Account ID >>>" + str);
    if (str != null) {
      purchaseHistory.setAccountId(str);
    }
  }

  public List<DomNode> scrapeDomList(String selector) {
    LOGGER.info("[scrapeDomList] in");
    List<DomNode> domList = page.querySelectorAll(selector);
    return domList;
  }

  public List<DomNode> scrapeDomList(DomNode node, String selector) {
    LOGGER.info("[scrapeDomList] in");
    List<DomNode> domList = node.querySelectorAll(selector);
    return domList;
  }

  public void scrapeOrderNumber(String selector) {
    LOGGER.info("[scrapeOrderNumber] in");
    String str = getText(selector);
    LOGGER.info("[scrapeOrderNumber] >>> Setting Order Number >>>" + str);
    if (str != null) {
      purchaseHistory.setOrderNumber(str);
    }
  }

  public void scrapeOrderNumber(DomNode node, String selector) {
    LOGGER.info("[scrapeOrderNumber] in");
    String str = getText(node, selector);
    LOGGER.info("[scrapeOrderNumber] >>> Setting Order Number >>>" + str);
    if (str != null) {
      purchaseHistory.setOrderNumber(str);
    }
  }


  public void scrapeOrderNumberWithRegex(DomNode node, String selector, String regexStr) {
    LOGGER.info("[scrapeOrderNumberWithRegex] in");

    String str  = getText(node, selector);
    Pattern ptn = Pattern.compile(regexStr, Pattern.DOTALL);
    str = extract(str, ptn);
    LOGGER.info("[scrapeOrderNumberWithRegex] >>> Setting Order Number >>>" + str);
    if (str != null) {
      purchaseHistory.setOrderNumber(str);
    }
  }

  public void scrapeOrderDate(String selector) {
    LOGGER.info("[scrapeOrderDate] in");
    String str = getText(selector);
    if (str != null) {
      try {
        purchaseHistory.setOrderDate(DateUtils.fromString(str));
      } catch (java.text.ParseException e) {
        LOGGER.info("[scrapeOrderDate] Could not set date for " + getText(selector) + " in NavigablePurchaseHistoryPage.java");
        e.printStackTrace();
      }
    }
  }

  public void scrapeOrderDate(DomNode node, String selector) {
    LOGGER.info("[scrapeOrderDate] in");
    String str = getText(node, selector);
    if (str != null) {
      try {
        purchaseHistory.setOrderDate(DateUtils.fromString(str));
      } catch (java.text.ParseException e) {
        LOGGER.info("[scrapeOrderDate] Could not set date for "
                + getText(node, selector) + " in NavigablePurchaseHistoryPage.java");
        e.printStackTrace();
      }
    }
  }

  public void scrapeOrderDateDefault(DomNode node, String selector) {
    LOGGER.info("[scrapeOrderDateDefault] in");
    String str = getText(node, selector);
    Date date  = extractDateDefault(str);
    if (date != null) {
      LOGGER.info("[scrapeOrderDateDefault] >>> Setting Order Date >>>" + date);
      purchaseHistory.setOrderDate(date);
    }
  }

  public void scrapeTotalAmount(DomNode node, String selector) {
    LOGGER.info("[scrapeTotalAmount] in");
    HtmlElement num = node.querySelector(selector);
    if (num != null) {
      Integer numInt = num != null ? extractInt(num.asText()) : null;
      LOGGER.info("[scrapeTotalAmount] >>> Setting Total Amount >>>" + numInt);
      purchaseHistory.setTotalAmount(Integer.toString(numInt));
    }
  }

  public void scrapeDeliveryStatus(DomNode node, String selector) {
    LOGGER.info("[scrapeDeliveryStatus] in");
    String str = getText(node, selector);
    LOGGER.info("[scrapeDeliveryStatus] >>> Setting Delivery Status >>>" + str);
    if (str != null) {
      purchaseHistory.setDeliveryStatus(str);
    }
  }

  public void scrapeProductCodeFromAnchor(DomNode node, String anchorSelector, String regexStr) {
    LOGGER.info("[scrapeProductCodeFromAnchor] in");
    LOGGER.info("WARNING: DEPRACATED. DO NOT USE");
    HtmlElement productAnchor = node.querySelector(anchorSelector);
    String productAnchorStr   = getAnchorHref(productAnchor);
    Pattern pattern           = Pattern.compile(regexStr);

    String str = extract1(productAnchorStr, pattern);
    //LOGGER.info("[scrapeProductCodeFromAnchor] >>> Setting Product Code >>>" + str);
    if (str != null) {
        productInfo.setCode(str);
    }
  }

  public void addProduct(ProductInfo product) {
    purchaseHistory.addProduct(product);
  }
    
    public void scrapeProductCode(String selector) {
        LOGGER.info("WARNING: DEPRACATED. DO NOT USE");
        String str = getText(selector);
        LOGGER.info("[scrapeProductCodeFromAnchor] >>> Setting Product Code >>>" + str);
        if (str != null) {
            productInfo.setCode(str);
        }
    }

    public void scrapeProductName(DomNode node, String selector) {
        LOGGER.info("WARNING: DEPRACATED. DO NOT USE");
        LOGGER.info("[scrapeProductName] in");
        String str = getText(node, selector);
        str = normalizeText(str);
        LOGGER.info("[scrapeProductName] >>> Setting Product Distributor >>>" + str);
        if (str != null) {
            productInfo.setName(str);
        }
    }

    public void scrapeProductNameFromAnchor(DomNode node, String anchorSelector) {
        LOGGER.info("WARNING: DEPRACATED. DO NOT USE");
        LOGGER.info("[scrapeProductNameFromAnchor] in");
        HtmlElement productAnchor = node.querySelector(anchorSelector);
        String str = getTextContent(productAnchor);
        LOGGER.info("[scrapeProductNameFromAnchor] >>> Setting Product Name >>>" + str);
        if (str != null) {
            productInfo.setName(str);
        }
    }

  public void scrapeUnitPrice(DomNode node, String selector) {
    LOGGER.info("[scrapeUnitPrice] in");
    HtmlElement num = node.querySelector(selector);
    if (num != null) {
      Integer numInt = num != null ? extractInt(num.asText()) : null;
      LOGGER.info("[scrapeUnitPrice] >>> Setting Unit Price >>>" + numInt);
      productInfo.setPrice(Integer.toString(numInt));
    }
  }

  public void scrapeProductQuantity(DomNode node, String selector) {
    LOGGER.info("[scrapeProductQuantity] in");
    HtmlElement num = node.querySelector(selector);
    if (num != null) {
      Integer numInt = num != null ? extractInt(num.asText()) : null;
      LOGGER.info("[scrapeProductQuantity] >>> Setting Product Quantity >>>" + numInt);
      productInfo.setQuantity(numInt);
    }
  }

  public void scrapeProductDistributor(DomNode node, String selector) {
    LOGGER.info("[scrapeProductDistributor] in");
    String str = getText(node, selector);
    LOGGER.info("[scrapeProductDistributor] >>> Setting Product Distributor >>>" + str);
    if (str != null) {
      productInfo.setDistributor(str);
    }
  }

  private String normalizeText(String str) {
    LOGGER.info("[normalizeText] in");
    if (str == null) {
      return str;
    }
    return str.trim().replaceAll("　", " ");
  }

  private static final Pattern DATE_DEFAULT_PATTERN = Pattern.compile("(20[\\d]{2}/[\\d]{2}/[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2})", Pattern.DOTALL);
  private static final String  DATE_DEFAULT_FORMAT  = "yyyy/MM/dd HH:mm:ss";

  private Date extractDateDefault(String str) {
    LOGGER.info("[extractDateDefault] in");
    return extractDate(str, DATE_DEFAULT_PATTERN, DATE_DEFAULT_FORMAT);
  }

  private Date extractDate(String str, Pattern pattern, String format) {
    LOGGER.info("[extractDate] in");
    String dateStr = extract(str, pattern);
    try {
      return DateUtils.fromString(dateStr, format);
    } catch (ParseException e) {
      LOGGER.error(String.format("[extractDate] Failed to parse the input '%s'. Error: %s", dateStr, e.getMessage()));
      e.printStackTrace();
      return null;
    }
  }

  protected Date extractDate(String text) {
    LOGGER.info("[extractDate] in");
    // Pattern PAT_DATE = Pattern.compile("(20[\\d]{2}/[\\d]{2}/[\\d]{2}
    // [\\d]{2}:[\\d]{2}:[\\d]{2})", Pattern.DOTALL);
    // LOGGER.info(">>>>> " + text);
    // String dateStr = text;//extract(text, PAT_DATE);
    // LOGGER.info(">>>>> " + dateStr);
    // String FORMAT_DATE = "yyyy/MM/dd HH:mm:ss";
    // dateStr = dateStr.replace("月", "/");
    // dateStr = dateStr.replace("日", "");
    // dateStr = dateStr.replace("年", "/");
    try {
      return DateUtils.fromString(text);// , FORMAT_DATE);
    } catch (Exception e) {
      // TODO: Logger
      // LOGGER.error(String.format("Failed to parse the input '%s'. Error: %s",
      // dateStr, e.getMessage()));
      e.printStackTrace();
      return null;
    }
  }

}
