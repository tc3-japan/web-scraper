package com.topcoder.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Purchase History Check Result model
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PurchaseHistoryCheckResultDetail {

    private final static ObjectMapper OB = new ObjectMapper();

    /**
     * Represents if result is passed
     */
    @JsonIgnore
    private boolean ok = true;

    /**
     * Represents check result for user id (email / telephone)
     */
    @JsonIgnore
    private String accountId;

    /**
     * Represents check result for order number
     */
    @JsonProperty("order_no")
    private String orderNumber;

    /**
     * Represents check result for order date
     */
    @JsonProperty("order_date")
    private String orderDate;

    /**
     * Represents check result for order total amount
     */
    @JsonProperty("total_amount")
    private String totalAmount;

    /**
     * Represents list of check result for ProductInfo
     */
    private List<ProductCheckResultDetail> products;

    /**
     * Represents check result for order delivery status
     */
    @JsonProperty("delivery_status")
    private String deliveryStatus;

    public PurchaseHistoryCheckResultDetail() {
    }

    public PurchaseHistoryCheckResultDetail(String accountId, String orderNumber, String orderDate, String totalAmount, String deliveryStatus) {
        this.accountId = accountId;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.deliveryStatus = deliveryStatus;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setProducts(List<ProductCheckResultDetail> products) {
        this.products = products;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public boolean isOk() {
        return ok;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public List<ProductCheckResultDetail> getProducts() {
        return products;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public String toJson() {
        try {
            return OB.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    public static String toArrayJson(List<PurchaseHistoryCheckResultDetail> list) {
        try {
            return OB.writeValueAsString(list);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }
}


