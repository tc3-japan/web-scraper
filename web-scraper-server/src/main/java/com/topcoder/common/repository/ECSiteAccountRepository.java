package com.topcoder.common.repository;

import com.topcoder.common.dao.ECSiteAccountDAO;

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

    List<ECSiteAccountDAO> findAllByEcSite(String ecSite);

    List<ECSiteAccountDAO> findAllByEcSiteAndUserIdIn(String ecSite, List<Integer> userIdList);
}
