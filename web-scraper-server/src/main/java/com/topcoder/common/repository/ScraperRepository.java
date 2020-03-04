package com.topcoder.common.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.topcoder.common.dao.ScraperDAO;

@Repository
public interface ScraperRepository extends CrudRepository<ScraperDAO, Integer> {

  @Query("select p from ScraperDAO p where p.site = :site and p.type = :type")
  ScraperDAO findBySiteAndType(@Param("site") String site, @Param("type") String type);

}
