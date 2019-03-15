package com.topcoder.scraper.dao;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "check_result")
public class CheckResultDAO {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /**
   * EC Site name
   */
  @Column(name = "ec_site", length = 32)
  private String ecSite;

  /**
   * Page name
   */
  @Column(name = "page", length = 32)
  private String page;

  /**
   * Page key
   */
  @Column(name = "page_key", length = 32)
  private String pageKey;

  /**
   * Check status
   */
  @Column(name = "total_check_status", length = 16)
  private String totalCheckStatus;

  /**
   * Check result detail
   */
  @Column(name = "check_result_detail", columnDefinition = "json")
  private String checkResultDetail;

  /**
   * Checked at
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "checked_at")
  private Date checkedAt;


  public CheckResultDAO() {
  }

  public CheckResultDAO(String ecSite, String page, String pageKey, String totalCheckStatus, String checkResultDetail, Date checkedAt) {
    this.ecSite = ecSite;
    this.page = page;
    this.pageKey = pageKey;
    this.totalCheckStatus = totalCheckStatus;
    this.checkResultDetail = checkResultDetail;
    this.checkedAt = checkedAt;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getEcSite() {
    return ecSite;
  }

  public void setEcSite(String ecSite) {
    this.ecSite = ecSite;
  }

  public String getPage() {
    return page;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public String getPageKey() {
    return pageKey;
  }

  public void setPageKey(String pageKey) {
    this.pageKey = pageKey;
  }

  public String getTotalCheckStatus() {
    return totalCheckStatus;
  }

  public void setTotalCheckStatus(String totalCheckStatus) {
    this.totalCheckStatus = totalCheckStatus;
  }

  public String getCheckResultDetail() {
    return checkResultDetail;
  }

  public void setCheckResultDetail(String checkResultDetail) {
    this.checkResultDetail = checkResultDetail;
  }

  public Date getCheckedAt() {
    return checkedAt;
  }

  public void setCheckedAt(Date checkedAt) {
    this.checkedAt = checkedAt;
  }
}
