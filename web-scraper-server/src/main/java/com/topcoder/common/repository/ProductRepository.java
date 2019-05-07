package com.topcoder.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.topcoder.common.dao.ProductDAO;

@Repository
public interface ProductRepository extends CrudRepository<ProductDAO, Integer> {
  
  ProductDAO findByProductCode(String productCode);
  
  @Query("select p from ProductDAO p where p.ecSite = :ecSite and p.productName = :productName")
  ProductDAO findByECSiteAndProductName(@Param("ecSite") String ecSite, @Param("productName") String productName);
  
  List<ProductDAO> findByFetchInfoStatusIsNull();
  
  @Query("select p from ProductDAO p where p.ecSite = :ecSite and p.fetchInfoStatus = null")
  List<ProductDAO> findByFetchInfoStatusAndECSite(@Param("ecSite") String ecSite);
}
