package com.topcoder.scraper.dao;

import com.topcoder.scraper.converter.JpaConverterPurchaseHistoryJson;
import com.topcoder.scraper.model.PurchaseHistory;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "purchase_history")
public class PurchaseHistoryDAO {

  /**
   * Purchase history id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /**
   * EC Site name
   */
  @Column(name = "ec_site", length = 32)
  private String ecSite;

  /**
   * UserDAO id (email / telephone)
   */
  @Column(name = "user_id", length = 64)
  private String userId;


  /**
   * ecSiteAccount Dao id
   */
  @Getter
  @Setter
  @Column(name = "ec_site_account_id")
  private Integer ecSiteAccountId;

  /**
   * Order number
   */
  @Column(name = "order_no")
  private String orderNo;

  /**
   * Order date
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "order_date")
  private Date orderDate;

  /**
   * Total amount
   */
  @Column(name = "total_amount")
  private String totalAmount;

  /**
   * Delivery status
   */
  @Column(name = "delivery_status")
  private String deliveryStatus;

  /**
   * Update at
   */
  @Column(name = "update_at")
  private Date updateAt;

  /**
   * Purchase history as json
   */
  @Column(name = "purchase_history_info", columnDefinition = "json")
  @Convert(converter = JpaConverterPurchaseHistoryJson.class)
  private PurchaseHistory purchaseHistory;

  @OneToMany(mappedBy = "purchaseHistory")
  private Set<PurchaseProductDAO> purchaseProducts = new HashSet<>();

  public PurchaseHistoryDAO(String ecSite, PurchaseHistory purchaseHistory) {
    this.ecSite = ecSite;
    this.purchaseHistory = purchaseHistory;
    this.userId = purchaseHistory.getUserId();
    this.orderNo = purchaseHistory.getOrderNumber();
    this.orderDate = purchaseHistory.getOrderDate();
    this.totalAmount = purchaseHistory.getTotalAmount();
    this.deliveryStatus = purchaseHistory.getDeliveryStatus();
    this.ecSiteAccountId = purchaseHistory.getEcSiteAccountId();
    this.updateAt = new Date();
  }

  public PurchaseHistoryDAO() {
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setPurchaseHistory(PurchaseHistory purchaseHistory) {
    this.purchaseHistory = purchaseHistory;
  }

  public void setEcSite(String ecSite) {
    this.ecSite = ecSite;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public void setOrderDate(Date orderDate) {
    this.orderDate = orderDate;
  }

  public void setTotalAmount(String totalAmount) {
    this.totalAmount = totalAmount;
  }

  public void setDeliveryStatus(String deliveryStatus) {
    this.deliveryStatus = deliveryStatus;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }

  public String getEcSite() {
    return ecSite;
  }

  public String getUserId() {
    return userId;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public Date getOrderDate() {
    return orderDate;
  }

  public String getTotalAmount() {
    return totalAmount;
  }

  public String getDeliveryStatus() {
    return deliveryStatus;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public Set<PurchaseProductDAO> getPurchaseProducts() {
    return purchaseProducts;
  }

  public int getId() {
    return id;
  }

  public PurchaseHistory getPurchaseHistory() {
    return purchaseHistory;
  }
}
