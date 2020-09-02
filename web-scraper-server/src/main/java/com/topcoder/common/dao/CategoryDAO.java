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

@Entity
@Table(name = "product_category")
public class CategoryDAO {

  /**
   * Category id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /**
   * EC Site name
   */
  @Column(name = "ec_site", length = 32)
  private String ecSite;

  /**
   * Category path
   */
  @Column(name = "category_path")
  private String categoryPath;

  /**
   * Update at
   */
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "update_at")
  private Date updateAt;

  public CategoryDAO() {
  }

  public CategoryDAO(String ecSite, String categoryPath, Date updateAt) {
    this.ecSite = ecSite;
    this.categoryPath = categoryPath;
    this.updateAt = updateAt;
  }

  public int getId() {
    return id;
  }

  public String getEcSite() {
    return ecSite;
  }

  public String getCategoryPath() {
    return categoryPath;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setEcSite(String ecSite) {
    this.ecSite = ecSite;
  }

  public void setCategoryPath(String categoryPath) {
    this.categoryPath = categoryPath;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }

}
