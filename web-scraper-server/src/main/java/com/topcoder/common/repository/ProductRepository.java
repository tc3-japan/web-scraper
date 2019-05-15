package com.topcoder.common.repository;

import java.util.List;

import com.topcoder.common.dao.ProductDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.topcoder.common.dao.ProductDAO;

@Repository
public interface ProductRepository extends CrudRepository<ProductDAO, Integer> {
  
  ProductDAO findById(Integer id);

  ProductDAO findByProductCode(String productCode);

  List<ProductDAO> findByFetchInfoStatusIsNull();
  
  @Query("select p from ProductDAO p where p.ecSite = :ecSite and p.productName = :productName")
  ProductDAO findByECSiteAndProductName(@Param("ecSite") String ecSite, @Param("productName") String productName);
  
  @Query("select p from ProductDAO p where p.ecSite = :ecSite and p.fetchInfoStatus = null")
  List<ProductDAO> findByFetchInfoStatusAndECSite(@Param("ecSite") String ecSite);

  List<ProductDAO> findByGroupStatusIsNullOrProductGroupIdIsNull();

  List<ProductDAO> findAllByProductGroupId(Integer id);

  Page<ProductDAO> findByProductGroupIdIsNullOrProductGroupIdIn(List<Integer> groupList, Pageable pageable);

  @Query("select p from ProductDAO p where (p.productGroupId is null or p.productGroupId in :groupIds) and (p.productName like %:keyword% or p.modelNo like %:keyword%)")
  Page<ProductDAO> findProducts(@Param("groupIds") List<Integer> groupIds, @Param("keyword") String keyword, Pageable pageable);
}
