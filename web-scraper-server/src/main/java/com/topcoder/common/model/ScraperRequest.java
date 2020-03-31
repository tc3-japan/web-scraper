package com.topcoder.common.model;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * the product group request
 */
@Data
public class ScraperRequest {


  /**
   * the script
   */
  @NotNull
  private String script;

}
