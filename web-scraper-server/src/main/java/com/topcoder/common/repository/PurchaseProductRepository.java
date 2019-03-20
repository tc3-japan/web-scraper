package com.topcoder.common.repository;

import com.topcoder.common.dao.PurchaseProductDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseProductRepository extends CrudRepository<PurchaseProductDAO, Integer> {
}
