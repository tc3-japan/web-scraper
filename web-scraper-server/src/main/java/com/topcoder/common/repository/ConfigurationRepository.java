package com.topcoder.common.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.topcoder.common.dao.ConfigurationDAO;

@Repository
public interface ConfigurationRepository extends CrudRepository<ConfigurationDAO, Integer> {

    @Query("select p from ConfigurationDAO p where p.site = :site and p.type = :type")
    ConfigurationDAO findBySiteAndType(@Param("site") String site, @Param("type") String type);

}
