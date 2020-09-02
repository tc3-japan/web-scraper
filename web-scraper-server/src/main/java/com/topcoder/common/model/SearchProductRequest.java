package com.topcoder.common.model;

import lombok.Data;


/**
 * the search product request
 */
@Data
public class SearchProductRequest {

  /**
   * the page number
   */
  private Integer pageNo;

  /**
   * the page size
   */
  private Integer pageSize;

  /**
   * the search keyword
   */
  private String searchKeyword;

  /**
   * the status
   */
  private String confirmationStatus;
}
