package com.topcoder.common.dao;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

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

  @ToString.Exclude
  @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
  private List<ECSiteAccountDAO> ecSiteAccountDAOS;

  public List<ECSiteAccountDAO> getECSiteAccountDAOS() {
    return ecSiteAccountDAOS;
  }
}
