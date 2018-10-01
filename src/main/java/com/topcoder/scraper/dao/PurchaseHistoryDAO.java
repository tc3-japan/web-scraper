package com.topcoder.scraper.dao;

import com.topcoder.scraper.converter.JpaConverterJson;
import com.topcoder.scraper.model.PurchaseHistory;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "purchase_history")
public class PurchaseHistoryDAO {

    /**
     * Purchase history id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Purchase history as json
     */
    @Column(name = "order_json", columnDefinition = "json")
    @Convert(converter = JpaConverterJson.class)
    private PurchaseHistory purchaseHistory;

    public PurchaseHistoryDAO(PurchaseHistory purchaseHistory) {
        this.purchaseHistory = purchaseHistory;
    }

    public PurchaseHistoryDAO() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPurchaseHistory(PurchaseHistory purchaseHistory) {
        this.purchaseHistory = purchaseHistory;
    }

    public int getId() {
        return id;
    }

    public PurchaseHistory getPurchaseHistory() {
        return purchaseHistory;
    }
}
