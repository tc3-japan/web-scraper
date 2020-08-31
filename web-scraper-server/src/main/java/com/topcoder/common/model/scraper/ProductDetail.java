
package com.topcoder.common.model.scraper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "item",
    "selector",
    "attribute",
    "regex",
    "label_selector",
    "label_value",
    "label_attribute",
    "label_regex",
    "script"
})
public class ProductDetail {
    @JsonProperty("item")
    private String item;
    @JsonProperty("selector")
    private String selector;
    @JsonProperty("attribute")
    private String attribute;
    @JsonProperty("regex")
    private String regex;
    @JsonProperty("is_script")
    private Boolean isScript;
    @JsonProperty("script")
    private String script;
    @JsonProperty("label_selector")
    private String labelSelector;
    @JsonProperty("label_value")
    private String labelValue;
    @JsonProperty("label_attribute")
    private String labelAttribute;
    @JsonProperty("label_regex")
    private String labelRegex;
}
