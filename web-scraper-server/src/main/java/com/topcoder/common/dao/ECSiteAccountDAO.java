package com.topcoder.common.dao;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topcoder.common.util.CipherUtils;

import lombok.Data;
import lombok.ToString;


/**
 * the ECSiteAccount DB entity
 */
@Entity
@Table(name = "ec_site_account")
@Data
@ToString(exclude = {"ecCookies", "password"})
public class ECSiteAccountDAO {
  /**
   * the id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /**
   * the ec site name
   */
  @Column(name = "ec_site")
  private String ecSite;

  /**
   * the ec use flag
   */
  @Column(name = "ec_use_flag")
  private Boolean ecUseFlag;

  /**
   * the user id
   */
  @Column(name = "user_id")
  private int userId;

  /**
   * the login email
   */
  @Column(name = "login_id_email")
  private String loginEmail;

  /**
   * the password
   */
  @JsonIgnore
  @Column(name = "password")
  private String password;

  /**
   * the cookies
   */
  //@JsonIgnore
  //@Column(name = "auth_cookies", columnDefinition = "MEDIUMTEXT")
  @Column(name = "auth_cookies", columnDefinition = "BLOB")
  //private String ecCookies;
  private byte[] ecCookies;

  /**
   * the auth status
   */
  @Column(name = "auth_status")
  private String authStatus;

  /**
   * the auth reason
   */
  @Column(name = "auth_fail_reason")
  private String authFailReason;

  /**
   * update at time
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "update_at")
  private Date updateAt;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "user_id", insertable=false, updatable=false)
  private UserDAO user;

  public String getLoginEmail() {
    return CipherUtils.decrypt(this.loginEmail);
  }

  public void setLoginEmail(String loginEmail) {
    this.loginEmail = CipherUtils.encrypt(loginEmail);
  }

  public String getPassword() {
    return CipherUtils.decrypt(this.password);
  }

  public void setPassword(String password) {
    this.password = CipherUtils.encrypt(password);
  }
}
