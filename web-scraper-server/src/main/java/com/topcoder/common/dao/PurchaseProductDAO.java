package com.topcoder.common.dao;

import com.topcoder.common.model.ProductInfo;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "purchase_product")
public class PurchaseProductDAO {

  /**
   * Product id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /**
   * Product code
   */
  @Column(name = "product_code")
  private String productCode;

  /**
   * Product name
   */
  @Column(name = "product_name")
  private String productName;

  /**
   * Product quantity
   */
  @Column(name = "product_quantity")
  private int productQuantity;

  /**
   * Unit price
   */
  @Column(name = "unit_price")
  private String unitPrice;

  /**
   * Product distributor
   */
  @Column(name = "product_distributor")
  private String productDistributor;

  /**
   * Related purchase history
   */
  @ManyToOne
  @JoinColumn(name = "purchase_history_id")
  private PurchaseHistoryDAO purchaseHistory;

  /**
   * Update at
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "update_at")
  private Date updateAt;

  public PurchaseProductDAO(ProductInfo productInfo, PurchaseHistoryDAO purchaseHistoryDAO) {
    this.productCode = productInfo.getCode();
    this.productName = productInfo.getName();
    this.productQuantity = productInfo.getQuantity();
    this.unitPrice = productInfo.getPrice();
    this.productDistributor = productInfo.getDistributor();
    this.purchaseHistory = purchaseHistoryDAO;
    this.updateAt = new Date();
  }

  public PurchaseProductDAO() {
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public void setProductQuantity(int productQuantity) {
    this.productQuantity = productQuantity;
  }

  public void setUnitPrice(String unitPrice) {
    this.unitPrice = unitPrice;
  }

  public void setProductDistributor(String productDistributor) {
    this.productDistributor = productDistributor;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }

  public int getId() {
    return id;
  }

  public String getProductCode() {
    return productCode;
  }

  public String getProductName() {
    return productName;
  }

  public int getProductQuantity() {
    return productQuantity;
  }

  public String getUnitPrice() {
    return unitPrice;
  }

  public String getProductDistributor() {
    return productDistributor;
  }

  public PurchaseHistoryDAO getPurchaseHistory() {
    return purchaseHistory;
  }

  public Date getUpdateAt() {
    return updateAt;
  }
}
