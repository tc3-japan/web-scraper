package com.topcoder.common.model.scraper;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * The Html page common selector node
 */
@Data
public class Selector {
  private String element;

  @JsonProperty("full_path")
  private Boolean fullPath;

  private String attribute;
  private String regex;

  @JsonProperty("is_script")
  private Boolean isScript;

  private String script;
}
