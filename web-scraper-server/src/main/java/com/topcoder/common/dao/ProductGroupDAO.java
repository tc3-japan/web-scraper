package com.topcoder.common.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

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
    public static final String jan_code = "Jan-Code-Grouping";
    public static final String product_name = "Product-Name-Grouping";
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
   * JAN code
   */
  @Column(name = "jan_code")
  private String janCode;

  /**
   * Product name
   */
  @Column(name = "product_name")
  private String productName;

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
