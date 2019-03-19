package com.topcoder.scraper.repository;

import com.topcoder.scraper.dao.CategoryDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<CategoryDAO, Integer> {
  CategoryDAO findByEcSiteAndCategoryPath(String ecSite, String categoryPath);
}
