package com.topcoder.common.dao;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * the user db entity
 */
@Entity
@Table(name = "user")
@Data
public class UserDAO {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /**
   * email for contact
   */
  @Column(name = "email_for_contact")
  private String emailForContact;

  /**
   * total ec status
   */
  @Column(name = "total_ec_status")
  private String totalECStatus;

  /**
   * the user id expired time
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "id_expire_at")
  private Date idExpireAt;

  /**
   * Checked at
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "update_at")
  private Date updateAt;
}
