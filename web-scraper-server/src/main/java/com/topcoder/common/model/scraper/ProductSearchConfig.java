
package com.topcoder.common.model.scraper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "url",
    "group_selector",
    "selector",
    "attribute",
    "regex",
    "script"
})
public class ProductSearchConfig {
    @JsonProperty("url")
    private String url;
    @JsonProperty("group_selector")
    private String groupSelector;
    @JsonProperty("selector")
    private String selector;
    @JsonProperty("attribute")
    private String attribute;
    @JsonProperty("regex")
    private String regex;
    @JsonProperty("script")
    private String script;
    @JsonProperty("excluded_selector")
    private String excludedSelector;
}
