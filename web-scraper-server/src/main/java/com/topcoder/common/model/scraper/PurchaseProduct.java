package com.topcoder.common.model.scraper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


/**
 * PurchaseProduct config class
 */
@Data
public class PurchaseProduct {
  @JsonProperty("url_element")
  private String urlElement;

  private String parent;

  @JsonProperty("product_code")
  private Selector productCode;

  @JsonProperty("product_name")
  private Selector productName;

  @JsonProperty("product_quantity")
  private Selector productQuantity;

  @JsonProperty("unit_price")
  private Selector unitPrice;

  @JsonProperty("product_distributor")
  private Selector productDistributor;
}
