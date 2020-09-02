package com.topcoder.common.repository;

import com.topcoder.common.dao.NormalDataDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalDataRepository extends CrudRepository<NormalDataDAO, Integer> {

    NormalDataDAO findFirstByEcSiteAndPageAndPageKey(String site, String page, String pageKey);
}
