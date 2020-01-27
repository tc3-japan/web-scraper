package com.topcoder.common.repository;

import com.topcoder.common.dao.ProductGroupDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductGroupRepository extends CrudRepository<ProductGroupDAO, Integer> {
    ProductGroupDAO getByModelNo(String modelNo);
    ProductGroupDAO getByJanCode(String janCode);
    ProductGroupDAO getByProductName(String janCode);
    Iterable<ProductGroupDAO> findByConfirmationStatus(String status);
}
