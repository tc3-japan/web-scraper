package com.topcoder.common.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * the product group request
 */
@Data
public class GroupRequest {


  /**
   * the model No
   */
  @NotNull
  private String modelNo;

  /**
   * the JAN Code
   */
  @NotNull
  private String janCode;

  /**
   * the Product Name
   */
  @NotNull
  private String productName;

  /**
   * the product id array
   */
  private List<Integer> productIds;

  /**
   * the group id
   */
  private List<Integer> groupIds;
}
