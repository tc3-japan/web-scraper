package com.topcoder.scraper.model;

/**
 * Product information model
 */
public class ProductInfo {

  /**
   * Represents product name
   */
  private String name;

  /**
   * Represents product price
   */
  private String price;

  /**
   * Represents product quantity
   */
  private String quantity;

  /**
   * Represents product distributor
   */
  private String distributor;

  public ProductInfo() {
  }

  public ProductInfo(String name, String price, String quantity, String distributor) {
    this.name = name;
    this.price = price;
    this.quantity = quantity;
    this.distributor = distributor;
  }

  public String getName() {
    return name;
  }

  public String getPrice() {
    return price;
  }

  public String getQuantity() {
    return quantity;
  }

  public String getDistributor() {
    return distributor;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public void setQuantity(String quantity) {
    this.quantity = quantity;
  }

  public void setDistributor(String distributor) {
    this.distributor = distributor;
  }
}
