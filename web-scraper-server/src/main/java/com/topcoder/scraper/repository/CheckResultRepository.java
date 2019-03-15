package com.topcoder.scraper.repository;

import com.topcoder.scraper.dao.CheckResultDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckResultRepository extends CrudRepository<CheckResultDAO, Integer> {

  CheckResultDAO findFirstByEcSiteAndPageAndPageKey(String site, String page, String pageKey);
}
