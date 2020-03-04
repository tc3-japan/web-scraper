package com.topcoder.api.service;

import com.topcoder.api.exception.EntityNotFoundException;
import com.topcoder.common.dao.ScraperDAO;
import com.topcoder.common.repository.ScraperRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * scraper service
 */
@Service
public class ScraperService {

  /**
   * the scraper Repository
   */
  @Autowired
  ScraperRepository scraperRepository;

  /**
   * get group by site and type
   *
   * @param site the ec site
   * @param type the logic
   * @return the db ScraperDAO
   * @throws EntityNotFoundException if not found
   */
  public ScraperDAO getScript(String site, String type) throws EntityNotFoundException {
	ScraperDAO scraperDAO = scraperRepository.findBySiteAndType(site, type);
    if (scraperDAO == null) {
      throw new EntityNotFoundException("Cannot found script where site = " + site + " and " + type);
    }
    return scraperDAO;
  }

}
