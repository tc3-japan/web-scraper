package com.topcoder.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topcoder.common.util.CipherUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Purchase History model
 */
public class PurchaseHistory {

  private final static ObjectMapper OB = new ObjectMapper();
  /**
   * Represents user id (email / telephone)
   */
  @JsonIgnore
  private String userId;

  /**
   * Represents order number
   */
  @JsonProperty("order_no")
  private String orderNumber;

  /**
   * Represents order date
   */
  @JsonProperty("order_date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private Date orderDate;

  /**
   * Represents order total amount
   */
  @JsonProperty("total_amount")
  private String totalAmount;

  /**
   * Represents list of ProductInfo
   */
  private List<ProductInfo> products;

  /**
   * Represents order delivery status
   * not implemented currently
   */
  @JsonProperty("delivery_status")
  private String deliveryStatus;

  public PurchaseHistory() {
  }

  public PurchaseHistory(String userId, String orderNumber, Date orderDate, String totalAmount, List<ProductInfo> products, String deliveryStatus) {
    this.userId = userId;
    this.orderNumber = orderNumber;
    this.orderDate = orderDate;
    this.totalAmount = totalAmount;
    this.products = products;
    this.deliveryStatus = deliveryStatus;
  }

  public String getUserId() {
    return userId;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public String getTotalAmount() {
    return totalAmount;
  }

  public List<ProductInfo> getProducts() {
    return products;
  }

  public String getDeliveryStatus() {
    return deliveryStatus;
  }

  public void setUserId(String userId) {
    this.userId = CipherUtils.md5(userId);
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }

  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }

  public void setTotalAmount(String totalAmount) {
    this.totalAmount = totalAmount;
  }

  public void setProducts(List<ProductInfo> products) {
    this.products = products;
  }

  public void setDeliveryStatus(String deliveryStatus) {
    this.deliveryStatus = deliveryStatus;
  }

  public static PurchaseHistory fromJson(String jsonString) {
    if (jsonString == null || jsonString.isEmpty()) {
      return null;
    }

    try {
      return OB.readValue(jsonString, PurchaseHistory.class);
    } catch (IOException e) {
      return null;
    }
  }

  public String toJson() {
    try {
      return OB.writeValueAsString(this);
    } catch (JsonProcessingException ex) {
      return null;
    }
  }

  public static List<PurchaseHistory> fromJsonToList(String jsonString) {
    if (jsonString == null || jsonString.isEmpty()) {
      return null;
    }

    try {
      return Arrays.asList(OB.readValue(jsonString, PurchaseHistory[].class));
    } catch (IOException e) {
      return null;
    }
  }

  public static String toArrayJson(List<PurchaseHistory> list) {
    try {
      return OB.writeValueAsString(list);
    } catch (JsonProcessingException ex) {
      return null;
    }
  }
}


