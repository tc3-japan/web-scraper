package com.topcoder.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedList;
import java.util.List;

/**
 * Product Check Result model
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ProductCheckResultDetail {
  private final static ObjectMapper OB = new ObjectMapper();

  /**
   * Represents if result is passed
   */
  @JsonIgnore
  private boolean ok = true;

  /**
   * Represents check result for product code
   */
  @JsonProperty("product_code")
  private String code;

  /**
   * Represents check result for product name
   */
  @JsonProperty("product_name")
  private String name;

  /**
   * Represents check result for product price
   */
  @JsonProperty("unit_price")
  private String price;

  /**
   * Represents check result for product quantity
   */
  @JsonProperty("product_quantity")
  private String quantity;

  /**
   * Represents check result for product distributor
   */
  @JsonProperty("product_distributor")
  private String distributor;

  /**
   * Represents check result for categories
   */
  @JsonProperty("categories")
  private List<String> categoryList = new LinkedList<>();

  public ProductCheckResultDetail() {
  }

  public ProductCheckResultDetail(String code, String name, String price, String quantity, String distributor) {
    this.code = code;
    this.name = name;
    this.price = price;
    this.quantity = quantity;
    this.distributor = distributor;
  }

  public boolean isOk() {
    return ok;
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

  public String getQuantity() {
    return quantity;
  }

  public String getDistributor() {
    return distributor;
  }

  public List<String> getCategoryList() {
    return categoryList;
  }

  public void setOk(boolean ok) {
    this.ok = ok;
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

  public void setCode(String code) {
    this.code = code;
  }

  public void setCategoryList(List<String> categoryList) {
    this.categoryList = categoryList;
  }

  public void addCategory(String category) {
    this.categoryList.add(category);
  }

  public String toJson() {
    try {
      return OB.writeValueAsString(this);
    } catch (JsonProcessingException ex) {
      return null;
    }
  }
}
