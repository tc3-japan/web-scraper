
package com.topcoder.common.model.scraper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import org.apache.xpath.operations.Bool;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "url",
        "group_selector",
        "selector",
        "excluded_selector",
        "attribute",
        "regex",
        "is_script",
        "script"
})
public class ProductSearchConfig {
    @JsonProperty("url")
    private String url;
    @JsonProperty("group_selector")
    private String groupSelector;
    @JsonProperty("selector")
    private String selector;
    @JsonProperty("excluded_selector")
    private String excludedSelector;
    @JsonProperty("attribute")
    private String attribute;
    @JsonProperty("regex")
    private String regex;
    @JsonProperty("is_script")
    private Boolean isScript;
    @JsonProperty("script")
    private String script;
}
