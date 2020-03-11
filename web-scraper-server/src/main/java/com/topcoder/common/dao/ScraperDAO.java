package com.topcoder.common.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "scraper")
public class ScraperDAO {

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
   * Sscript
   */
  @Column(name = "script", columnDefinition = "LONGTEXT")
  private String script;

  public ScraperDAO() {
  }

  public ScraperDAO(String site, String type, String script) {
	this.site = site;
	this.type = type;
	this.script = script;
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

  public void setScript(String script) {
    this.script = script;
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

  public String getScript() {
    return script;
  }

}
