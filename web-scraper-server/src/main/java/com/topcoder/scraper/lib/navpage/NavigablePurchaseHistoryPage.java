package com.topcoder.scraper.lib.navpage;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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

import static com.topcoder.common.util.HtmlUtils.*;

public class NavigablePurchaseHistoryPage extends NavigablePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigablePurchaseHistoryPage.class);

    // TrafficWebClient webClient;
    // HtmlPage page;
    @Getter@Setter private PurchaseHistory purchaseHistory;
    @Getter@Setter private ProductInfo     productInfo ;

    public NavigablePurchaseHistoryPage(HtmlPage page, TrafficWebClient webClient) {
        super(page, webClient);
    }

    public NavigablePurchaseHistoryPage(String url, TrafficWebClient webClient) {
        super(url, webClient);
    }

    public NavigablePurchaseHistoryPage(TrafficWebClient webClient) {
        super((HtmlPage)null, webClient);
    }

    public void scrapeAccountId(String selector) {
        String str = getText(selector);
        //LOGGER.info(" >>> Setting Account ID >>>" + str);
        if (str != null) {
            purchaseHistory.setAccountId(str);
        }
    }

    public void scrapeAccountId(DomNode node, String selector) {
        String str = getText(node, selector);
        //LOGGER.info(" >>> Setting Account ID >>>" + str);
        if (str != null) {
            purchaseHistory.setAccountId(str);
        }
    }

    public List<DomNode> scrapeDomList(String selector) {
        return page.querySelectorAll(selector);
    }

    public void scrapeOrderNumber(String selector) {
        String str = getText(selector);
        LOGGER.info(" >>> Setting Order Number >>>" + str);
        if (str != null) {
            purchaseHistory.setOrderNumber(str);
        }
    }

    public void scrapeOrderNumber(DomNode node, String selector) {
        String str = getText(node, selector);
        LOGGER.info(" >>> Setting Order Number >>>" + str);
        if (str != null) {
            purchaseHistory.setOrderNumber(str);
        }
    }

    public void scrapeOrderNumberWithRegex(DomNode node, String selector, String regexStr) {
        String str  = getText(node, selector);
        Pattern ptn = Pattern.compile(regexStr, Pattern.DOTALL);
        str = extract(str, ptn);
        LOGGER.info(" >>> Setting Order Number >>>" + str);
        if (str != null) {
            purchaseHistory.setOrderNumber(str);
        }
    }

    public void scrapeOrderDate(String selector) {
        String str = getText(selector);
        if (str != null) {
            try {
                purchaseHistory.setOrderDate(DateUtils.fromString(str));
            } catch (java.text.ParseException e) {
                LOGGER.info(
                        "Could not set date for " + getText(selector) + " in NavigablePurchaseHistoryPage.java");
                e.printStackTrace();
            }
        }
    }

    public void scrapeOrderDate(DomNode node, String selector) {
        String str = getText(node, selector);
        if (str != null) {
            try {
                purchaseHistory.setOrderDate(DateUtils.fromString(str));
            } catch (java.text.ParseException e) {
                LOGGER.info(
                        "Could not set date for " + getText(node, selector) + " in NavigablePurchaseHistoryPage.java");
                e.printStackTrace();
            }
        }
    }

    public void scrapeOrderDateDefault(DomNode node, String selector) {
        String str = getText(node, selector);
        Date date  = extractDateDefault(str);
        if (date != null) {
            LOGGER.info(" >>> Setting Order Date >>>" + date);
            purchaseHistory.setOrderDate(date);
        }
    }

    public void scrapeTotalAmount(DomNode node, String selector) {
        HtmlElement num = node.querySelector(selector);
        if (num != null) {
            Integer numInt = num != null ? extractInt(num.asText()) : null;
            LOGGER.info(" >>> Setting Total Amount >>>" + numInt);
            purchaseHistory.setTotalAmount(Integer.toString(numInt));
        }
    }

    public void scrapeDeliveryStatus(DomNode node, String selector) {
        String str = getText(node, selector);
        LOGGER.info(" >>> Setting Delivery Status >>>" + str);
        if (str != null) {
            purchaseHistory.setDeliveryStatus(str);
        }
    }

    public void scrapeProductCodeFromAnchor(DomNode node, String anchorSelector, String regexStr) {
        HtmlElement productAnchor = node.querySelector(anchorSelector);
        String productAnchorStr   = getAnchorHref(productAnchor);
        Pattern pattern           = Pattern.compile(regexStr);

        String str = extract1(productAnchorStr, pattern);
        LOGGER.info(" >>> Setting Product Code >>>" + str);
        if (str != null) {
            productInfo.setCode(str);
        }
    }

    public void scrapeProductName(DomNode node, String selector) {
        String str = getText(node, selector);
        str = normalizeText(str);
        LOGGER.info(" >>> Setting Product Distributor >>>" + str);
        if (str != null) {
            productInfo.setName(str);
        }
    }

    public void scrapeProductNameFromAnchor(DomNode node, String anchorSelector) {
        HtmlElement productAnchor = node.querySelector(anchorSelector);
        String str = getTextContent(productAnchor);
        LOGGER.info(" >>> Setting Product Name >>>" + str);
        if (str != null) {
            productInfo.setName(str);
        }
    }

    public void scrapeUnitPrice(DomNode node, String selector) {
        HtmlElement num = node.querySelector(selector);
        if (num != null) {
            Integer numInt = num != null ? extractInt(num.asText()) : null;
            LOGGER.info(" >>> Setting Unit Price >>>" + numInt);
            productInfo.setPrice(Integer.toString(numInt));
        }
    }

    public void scrapeProductQuantity(DomNode node, String selector) {
        HtmlElement num = node.querySelector(selector);
        if (num != null) {
            Integer numInt = num != null ? extractInt(num.asText()) : null;
            LOGGER.info(" >>> Setting Product Quantity >>>" + numInt);
            productInfo.setQuantity(numInt);
        }
    }

    public void scrapeProductDistributor(DomNode node, String selector) {
        String str = getText(node, selector);
        LOGGER.info(" >>> Setting Product Distributor >>>" + str);
        if (str != null) {
            productInfo.setDistributor(str);
        }
    }

    private String normalizeText(String str) {
        if (str == null) {
            return str;
        }
        return str.trim().replaceAll("　", " ");
    }

    private static final Pattern DATE_DEFAULT_PATTERN = Pattern.compile("(20[\\d]{2}/[\\d]{2}/[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2})", Pattern.DOTALL);
    private static final String  DATE_DEFAULT_FORMAT  = "yyyy/MM/dd HH:mm:ss";

    private Date extractDateDefault(String str) {
        return extractDate(str, DATE_DEFAULT_PATTERN, DATE_DEFAULT_FORMAT);
    }

    private Date extractDate(String str, Pattern pattern, String format) {
        String dateStr = extract(str, pattern);
        try {
            return DateUtils.fromString(dateStr, format);
        } catch (ParseException e) {
            LOGGER.error(String.format("Failed to parse the input '%s'. Error: %s", dateStr, e.getMessage()));
            e.printStackTrace();
            return null;
        }
    }

    protected Date extractDate(String text) {
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
