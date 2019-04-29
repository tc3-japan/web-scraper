package com.topcoder.common.dao;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * product group DAO
 */
@Data
@Entity
@Table(name = "product_group")
public class ProductGroupDAO {


  /**
   * the product group confirmation status
   */
  public static class ConfirmationStatus {
    public static final String confirmed = "confirmed";
    public static final String unconfirmed = "unconfirmed";
  }

  /**
   * the product group method
   */
  public static class GroupingMethod {
    public static final String manual = "Manual-Grouping";
    public static final String same_no = "Model-No-Grouping";
  }

  /**
   * Product group id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /**
   * group model number
   */
  @Column(name = "model_no")
  private String modelNo;


  /**
   * the group method
   */
  @Column(name = "grouping_method")
  private String groupingMethod;

  /**
   * the confirmation status
   */
  @Column(name = "confirmation_status")
  private String confirmationStatus = ConfirmationStatus.unconfirmed;

  /**
   * Update at
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "update_at")
  @UpdateTimestamp
  private Date updateAt;

}
