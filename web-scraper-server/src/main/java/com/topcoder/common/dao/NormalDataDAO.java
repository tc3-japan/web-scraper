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
@Table(name = "normal_data")
public class NormalDataDAO {
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
     * Normal data json
     */
    @Column(name = "normal_data", columnDefinition = "json")
    private String normalData;

    /**
     * Data downloaded at
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "downloaded_at")
    private Date downloadedAt;


    public NormalDataDAO() {
    }

    public NormalDataDAO(String ecSite, String page, String pageKey, String normalData, Date downloadedAt) {
        this.ecSite = ecSite;
        this.page = page;
        this.pageKey = pageKey;
        this.normalData = normalData;
        this.downloadedAt = downloadedAt;
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

    public String getNormalData() {
        return normalData;
    }

    public void setNormalData(String normalData) {
        this.normalData = normalData;
    }

    public Date getDownloadedAt() {
        return downloadedAt;
    }

    public void setDownloadedAt(Date downloadedAt) {
        this.downloadedAt = downloadedAt;
    }


}
