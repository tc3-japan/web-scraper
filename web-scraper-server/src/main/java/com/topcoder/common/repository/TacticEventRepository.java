package com.topcoder.common.repository;

import com.topcoder.common.dao.TacticEventDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TacticEventRepository extends CrudRepository<TacticEventDAO, Integer> {
}
