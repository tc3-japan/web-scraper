package com.topcoder.common.repository;

import com.topcoder.common.dao.UserDAO;
import org.springframework.data.repository.CrudRepository;

/**
 * the user repository
 */
public interface UserRepository extends CrudRepository<UserDAO, Integer> {


}
