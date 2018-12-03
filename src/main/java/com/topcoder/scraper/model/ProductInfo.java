package com.topcoder.scraper.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Product information model
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ProductInfo {

  /**
   * Represents product code
   */
  @JsonProperty("product_code")
  private String code;

  /**
   * Represents product name
   */
  @JsonProperty("product_name")
  private String name;

  /**
   * Represents product price
   */
  @JsonProperty("unit_price")
  private String price;

  /**
   * Represents product quantity
   */
  @JsonProperty("product_quantity")
  private int quantity;

  /**
   * Represents product distributor
   */
  @JsonProperty("product_distributor")
  private String distributor;

  public ProductInfo() {
  }

  public ProductInfo(String code, String name, String price, Integer quantity, String distributor) {
    this.code = code;
    this.name = name;
    this.price = price;
    if (quantity != null) {
      this.quantity = quantity;
    }
    this.distributor = distributor;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getPrice() {
    return price;
  }

  public int getQuantity() {
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

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public void setDistributor(String distributor) {
    this.distributor = distributor;
  }
}
