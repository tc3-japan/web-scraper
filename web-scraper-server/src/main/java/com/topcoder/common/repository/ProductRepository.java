package com.topcoder.common.repository;

import com.topcoder.common.dao.ProductDAO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<ProductDAO, Integer> {
  ProductDAO findByProductCode(String productCode);

  ProductDAO findById(Integer id);

  List<ProductDAO> findByFetchInfoStatusIsNull();

  List<ProductDAO> findByGroupStatusIsNullOrProductGroupIdIsNull();

  List<ProductDAO> findAllByProductGroupId(Integer id);

  Page<ProductDAO> findByProductGroupIdIsNullOrProductGroupIdIn(List<Integer> groupList, Pageable pageable);

  @Query("select p from ProductDAO p where (p.productGroupId is null or p.productGroupId in :groupIds) and (p.productName like %:keyword% or p.modelNo like %:keyword%)")
  Page<ProductDAO> findProducts(@Param("groupIds") List<Integer> groupIds, @Param("keyword") String keyword, Pageable pageable);
}
