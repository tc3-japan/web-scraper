package com.topcoder.common.model.scraper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * PurchaseOrder config class
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PurchaseOrder extends PurchaseCommon {
  @JsonProperty("purchase_product")
  private PurchaseProduct purchaseProduct;
}
