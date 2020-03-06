package com.topcoder.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.topcoder.api.exception.ApiException;
import com.topcoder.api.exception.EntityNotFoundException;
import com.topcoder.common.dao.ScraperDAO;
import com.topcoder.common.repository.ScraperRepository;


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
   * @param type the logic type
   * @return the db ScraperDAO
   * @throws EntityNotFoundException if not found
   */
  public ScraperDAO getScript(String site, String type) throws EntityNotFoundException {
	  return get(site, type);
  }

  /**
   * update ScraperDAO
   *
   * @param site the ec site
   * @param type the logic type
   * @param entity the request entity
   * @throws ApiException if ScraperDAO not found
   */
  public void updateScript(String site, String type, ScraperDAO entity) throws ApiException {
	  ScraperDAO scraperDAO = get(site, type);
	  scraperDAO.setSite(site);
	  scraperDAO.setType(type);
	  scraperDAO.setScript(entity.getScript());
	  scraperRepository.save(scraperDAO);
  }

  /**
   * get ScraperDAO by site and type
   *
   * @param site the ec site
   * @param site the logic type
   * @return the entity
   */
  public ScraperDAO get(String site, String type) throws EntityNotFoundException {
	ScraperDAO scraperDAO = scraperRepository.findBySiteAndType(site, type);
    if (scraperDAO == null) {
      throw new EntityNotFoundException("Cannot found script where site = " + site + " and " + type);
    }
    return scraperDAO;
  }

}
