package com.topcoder.scraper.repository;

import com.topcoder.scraper.dao.PurchaseHistoryDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseHistoryRepository extends CrudRepository<PurchaseHistoryDAO, Integer> {
}
