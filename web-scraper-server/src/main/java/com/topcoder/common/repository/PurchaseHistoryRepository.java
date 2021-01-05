package com.topcoder.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.topcoder.common.dao.PurchaseHistoryDAO;

@Repository
public interface PurchaseHistoryRepository extends CrudRepository<PurchaseHistoryDAO, Integer> {

    @Query("select h from PurchaseHistoryDAO h where h.accountId = :accountId order by h.orderDate desc")
    public List<PurchaseHistoryDAO> getPurchaseHistoriesByAccountIdOrderByOrderDateDesc(@Param("accountId") String accountId);

    List<PurchaseHistoryDAO> getByEcSiteAndOrderNo(String site, String orderNo);
}
