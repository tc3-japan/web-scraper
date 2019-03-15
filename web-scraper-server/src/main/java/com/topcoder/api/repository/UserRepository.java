package com.topcoder.api.repository;

import com.topcoder.api.dao.UserDAO;
import org.springframework.data.repository.CrudRepository;

/**
 * the user repository
 */
public interface UserRepository extends CrudRepository<UserDAO, Integer> {


}
