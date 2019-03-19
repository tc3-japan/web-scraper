package com.topcoder.api.repository;

import com.topcoder.api.dao.ECSiteAccountDAO;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * the ECSiteAccount Repository
 */
public interface ECSiteAccountRepository extends CrudRepository<ECSiteAccountDAO, Integer> {

  /**
   * find all ec site account by user id
   *
   * @param userId the user id
   * @return the list
   */
  List<ECSiteAccountDAO> findAllByUserId(Integer userId);
}
