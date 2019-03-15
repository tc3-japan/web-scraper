package com.topcoder.api.dao;

import lombok.Data;

import javax.persistence.*;
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
