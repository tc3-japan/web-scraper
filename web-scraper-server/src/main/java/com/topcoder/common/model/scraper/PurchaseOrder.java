package com.topcoder.common.model.scraper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


/**
 * PurchaseOrder config class
 */
@Data
public class PurchaseOrder {
  @JsonProperty("url_element")
  private String urlElement;

  private String parent;

  @JsonProperty("order_number")
  private Selector orderNumber;

  @JsonProperty("order_date")
  private Selector orderDate;

  @JsonProperty("total_amount")
  private Selector totalAmount;

  @JsonProperty("delivery_status")
  private Selector deliveryStatus;

  @JsonProperty("purchase_product")
  private PurchaseProduct purchaseProduct;
}
