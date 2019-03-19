package com.topcoder.common.repository;

import com.topcoder.common.dao.CategoryDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<CategoryDAO, Integer> {
  CategoryDAO findByEcSiteAndCategoryPath(String ecSite, String categoryPath);
}
