package com.topcoder.scraper.model;

import java.util.List;

/**
 * Purchase History model
 */
public class PurchaseHistory {

  /**
   * Represents order number
   */
  private String orderNumber;

  /**
   * Represents order date
   */
  private String orderDate;

  /**
   * Represents order total amount
   */
  private String totalAmount;

  /**
   * Represents list of ProductInfo
   */
  private List<ProductInfo> products;

  /**
   * Represents order delivery status
   * not implemented currently
   */
  private String deliveryStatus;

  public PurchaseHistory() {
  }

  public PurchaseHistory(String orderNumber, String orderDate, String totalAmount, List<ProductInfo> products, String deliveryStatus) {
    this.orderNumber = orderNumber;
    this.orderDate = orderDate;
    this.totalAmount = totalAmount;
    this.products = products;
    this.deliveryStatus = deliveryStatus;
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

  public List<ProductInfo> getProducts() {
    return products;
  }

  public String getDeliveryStatus() {
    return deliveryStatus;
  }
}
