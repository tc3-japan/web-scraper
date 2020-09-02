package com.topcoder.common.model.scraper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PurchaseCommon {
    @JsonProperty("url_element")
    protected String urlElement;

    protected String parent;

    @JsonProperty("order_number")
    protected Selector orderNumber;

    @JsonProperty("order_date")
    protected Selector orderDate;

    @JsonProperty("total_amount")
    protected Selector totalAmount;

    @JsonProperty("delivery_status")
    protected Selector deliveryStatus;

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
