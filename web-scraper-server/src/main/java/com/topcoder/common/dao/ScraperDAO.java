package com.topcoder.common.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.util.Common;
import com.topcoder.scraper.converter.JpaConverterPurchaseInfoJson;

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
   * type(logic) name
   */
  @Column(name = "type", length = 32)
  private String type;

  /**
   * script
   */
  @Column(name = "script", columnDefinition = "TEXT")
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

  public void seSite(String site) {
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
