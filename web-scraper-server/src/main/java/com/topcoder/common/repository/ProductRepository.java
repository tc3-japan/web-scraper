package com.topcoder.common.repository;

import com.topcoder.common.dao.ProductDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<ProductDAO, Integer> {
  ProductDAO findByProductCode(String productCode);
  List<ProductDAO> findByFetchInfoStatusIsNull();
}
