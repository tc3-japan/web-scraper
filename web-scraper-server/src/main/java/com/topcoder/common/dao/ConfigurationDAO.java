package com.topcoder.common.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "configuration")
public class ConfigurationDAO {

  /**
   * Id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /**
   * EC Site name
   */
  @Column(name = "site", length = 32)
  private String site;

  /**
   * Logic type name
   */
  @Column(name = "type", length = 32)
  private String type;

  /**
   * Config
   */
  @Column(name = "config", columnDefinition = "LONGTEXT")
  private String config;

  public ConfigurationDAO() {
  }

  public ConfigurationDAO(String site, String type, String config) {
	this.site = site;
	this.type = type;
	this.config = config;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public int getId() {
    return id;
  }

  public String getSite() {
    return site;
  }

  public String getType() {
    return type;
  }

  public String getConfig() {
    return config;
  }

}
