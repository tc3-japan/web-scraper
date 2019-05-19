package com.topcoder.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Product information model
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ProductInfo {
  private final static ObjectMapper OB = new ObjectMapper();
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

  @JsonProperty("categories")
  private List<String> categoryList = new ArrayList<>();

  @JsonProperty("rankings")
  private List<Integer> rankingList = new ArrayList<>();
  
  @JsonProperty("moedel_no")
  private String modelNo;;

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

  public List<String> getCategoryList() {
    return categoryList;
  }

  public List<Integer> getRankingList() {
    return rankingList;
  }
  
  public String getModelNo() {
	  return modelNo;
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

  public void setCode(String code) {
    this.code = code;
  }

  public void setCategoryList(List<String> categoryList) {
    this.categoryList = categoryList;
  }

  public void setRankingList(List<Integer> rankingList) {
    this.rankingList = rankingList;
  }
  
  public void setModelNo(String modelNo) {
	    this.modelNo = modelNo;
  }

  public void addCategoryRanking(String category, int ranking) {
    this.categoryList.add(category);
    this.rankingList.add(ranking);
  }

  public static ProductInfo fromJson(String jsonString) {
    if (jsonString == null || jsonString.isEmpty()) {
      return null;
    }

    try {
      return OB.readValue(jsonString, ProductInfo.class);
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
}
