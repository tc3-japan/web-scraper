package com.topcoder.common.model.scraper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * purchase history config class
 */
@Data
public class PurchaseHistoryConfig {
    private String url;

    @JsonProperty("order_filter")
    private Selector orderFilter;

    @JsonProperty("purchase_order")
    private PurchaseOrder purchaseOrder;

    @JsonProperty("next_url_element")
    private String nextUrlElement;
}
