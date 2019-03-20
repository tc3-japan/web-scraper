package com.topcoder.common.repository;

import com.topcoder.common.dao.PurchaseHistoryDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseHistoryRepository extends CrudRepository<PurchaseHistoryDAO, Integer> {
}
