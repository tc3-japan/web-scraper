package com.topcoder.common.repository;

import com.topcoder.common.dao.RequestEventDAO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestEventRepository extends CrudRepository<RequestEventDAO, Integer> {
}
