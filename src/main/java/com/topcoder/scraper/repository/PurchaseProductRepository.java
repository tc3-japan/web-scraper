package com.topcoder.scraper.repository;

import com.topcoder.scraper.dao.PurchaseProductDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseProductRepository extends CrudRepository<PurchaseProductDAO, Integer> {
}
