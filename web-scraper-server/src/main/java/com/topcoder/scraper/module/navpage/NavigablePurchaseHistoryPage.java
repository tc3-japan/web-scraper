package com.topcoder.scraper.module.navpage;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.DateUtils;

import static com.topcoder.common.util.HtmlUtils.*;

import java.util.Date;

public class NavigablePurchaseHistoryPage extends NavigablePage {

    // TrafficWebClient webClient;
    // HtmlPage page;
    PurchaseHistory purchaseHistory;

    public NavigablePurchaseHistoryPage(HtmlPage page, TrafficWebClient webClient, PurchaseHistory purchaseHistory) {
        super(page, webClient);
        this.purchaseHistory = purchaseHistory;
    }

    public NavigablePurchaseHistoryPage(String url, TrafficWebClient webClient, PurchaseHistory purchaseHistory) {
        super(url, webClient);
        this.purchaseHistory = purchaseHistory;
    }

    public void setAccountId(String selector) {
        String str = getText(selector);
        //System.out.println(" >>> Setting Account ID >>>" + str);
        if (str != null) {
            purchaseHistory.setAccountId(str);
        }
    }

    public void setAccountId(DomNode node, String selector) {
        String str = getText(node, selector);
        //System.out.println(" >>> Setting Account ID >>>" + str);
        if (str != null) {
            purchaseHistory.setAccountId(str);
        }
    }

    public void setOrderNumber(String selector) {
        String str = getText(selector);
        System.out.println(" >>> Setting Order Number >>>" + str);
        if (str != null) {
            purchaseHistory.setOrderNumber(str);
        }
    }

    public void setOrderNumber(DomNode node, String selector) {
        String str = getText(node, selector);
        System.out.println(" >>> Setting Order Number >>>" + str);
        if (str != null) {
            purchaseHistory.setOrderNumber(str);
        }
    }

    public void setOrderDate(String selector) {
        String str = getText(selector);
        if (str != null) {
            try {
                purchaseHistory.setOrderDate(DateUtils.fromString(str));
            } catch (java.text.ParseException e) {
                System.out.println(
                        "Could not set date for " + getText(selector) + " in NavigablePurchaseHistoryPage.java");
                e.printStackTrace();
            }
        }
    }

    public void setOrderDate(DomNode node, String selector) {
        String str = getText(node, selector);
        if (str != null) {
            try {
                purchaseHistory.setOrderDate(DateUtils.fromString(str));
            } catch (java.text.ParseException e) {
                System.out.println(
                        "Could not set date for " + getText(node, selector) + " in NavigablePurchaseHistoryPage.java");
                e.printStackTrace();
            }
        }
    }

    public void setPrice(String selector) {
        HtmlElement totalAmountNode = page
                .querySelector("#total > ul:nth-child(2) > li:nth-child(4) > dl:nth-child(1) > dd:nth-child(2)");
        if (totalAmountNode != null) {
            Integer totalAmount = totalAmountNode != null ? extractInt(totalAmountNode.asText()) : null;
            System.out.println(" >>> Setting Total Amount >>>" + totalAmount);
            purchaseHistory.setTotalAmount(Integer.toString(totalAmount));
        }
    }

    public void setPrice(DomNode node, String selector) {
        HtmlElement totalAmountNode = node
                .querySelector("#total > ul:nth-child(2) > li:nth-child(4) > dl:nth-child(1) > dd:nth-child(2)");
        if (totalAmountNode != null) {
            Integer totalAmount = totalAmountNode != null ? extractInt(totalAmountNode.asText()) : null;
            System.out.println(" >>> Setting Total Amount >>>" + totalAmount);
            purchaseHistory.setTotalAmount(Integer.toString(totalAmount));
        }
    }

    public PurchaseHistory getPurchaseHistory() {
        return purchaseHistory;
    }

    protected Date extractDate(String text) {
        // Pattern PAT_DATE = Pattern.compile("(20[\\d]{2}/[\\d]{2}/[\\d]{2}
        // [\\d]{2}:[\\d]{2}:[\\d]{2})", Pattern.DOTALL);
        // System.out.println(">>>>> " + text);
        // String dateStr = text;//extract(text, PAT_DATE);
        // System.out.println(">>>>> " + dateStr);
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