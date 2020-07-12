package com.topcoder.common.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Html Path model
 */
public class HtmlPath {

  /**
   * lists of html path
   */
  @JsonProperty("urls")
  private List<String> htmlPathList;

  public HtmlPath(List<String> htmlPathList) {
    this.htmlPathList = htmlPathList;
  }

}


