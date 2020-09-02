package com.topcoder.common.repository;

import com.topcoder.common.dao.CheckResultDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckResultRepository extends CrudRepository<CheckResultDAO, Integer> {

    CheckResultDAO findFirstByEcSiteAndPageAndPageKey(String site, String page, String pageKey);
}
