package com.topcoder.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.topcoder.api.exception.ApiException;
import com.topcoder.api.exception.EntityNotFoundException;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.dao.ScraperDAO;
import com.topcoder.common.model.ScraperRequest;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.ScraperRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.Common;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralPurchaseHistoryCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralPurchaseHistoryCrawlerResult;
import org.slf4j.Logger;
import com.topcoder.common.model.PurchaseHistory;
import java.util.List;
import com.topcoder.common.model.CrawlerContext;

import com.topcoder.scraper.service.impl.FileBasedWebpageService;

import com.topcoder.scraper.module.ecunifiedmodule.DryRunPurchaseHistoryModule;

import java.util.ArrayList;

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
   * ec site account repository
   */
  @Autowired
  ECSiteAccountRepository ecSiteAccountRepository;

  @Autowired
  DryRunPurchaseHistoryModule dryRunPurchaseHistoryModule;

  /**
   * get script by site and type
   *
   * @param site the ec site
   * @param type the logic type
   * @return the script text
   * @throws EntityNotFoundException if not found
   */
  public String getScript(String site, String type) throws EntityNotFoundException {
	ScraperDAO scraperDAO = get(site, type);
    if (scraperDAO == null) {
      throw new EntityNotFoundException("Cannot found script where site = " + site + " and " + type);
    }
	return get(site, type).getScript();
  }

  /**
   * create or update ScraperDAO
   *
   * @param site the ec site
   * @param type the logic type
   * @param entity the request entity
   * @return the result message text
   * @throws ApiException if any error happened
   */
  public String createOrUpdateScript(String site, String type, ScraperDAO entity) throws ApiException {
	try {
	  String resultText = "success ";

	  ScraperDAO scraperDAO = get(site, type);

	  if (scraperDAO == null) {
	    scraperDAO = new ScraperDAO();
	    resultText += "create record to scraper table";
	  } else {
	    resultText += "update record to scraper table";
	  }

      scraperDAO.setSite(site);
      scraperDAO.setType(type);
      scraperDAO.setScript(entity.getScript());
      scraperRepository.save(scraperDAO);

      return resultText;

    } catch(Exception e) {
      e.printStackTrace();
	  throw new ApiException("failed to create or update script");
    }
  }

  /**
   * execute script
   *
   * @param site the ec site
   * @param type the logic type
   * @param request to executable script
   * @throws ApiException if any error happened
   */
  public List<PurchaseHistory> executeScript(String site, String type, ScraperRequest request) throws ApiException {
    try {
      String script = request.getScript();
      //String script = getScript(site, type); // from DB table [scraper]
	  dryRunPurchaseHistoryModule.setScript(script);

	  List<String> sites = new ArrayList();
	  sites.add(site);

	  dryRunPurchaseHistoryModule.fetchPurchaseHistoryList(sites);

	  List<PurchaseHistory> list = dryRunPurchaseHistoryModule.getPurchaseHistoryList();

	  return list;
    } catch(Exception e) {
      e.printStackTrace();
	  throw new ApiException("failed to execute script");
    }
  }

  /**
   * get ScraperDAO by site and type
   *
   * @param site the ec site
   * @param site the logic type
   * @return the ScraperDAO
   */
  public ScraperDAO get(String site, String type) {
	ScraperDAO scraperDAO = scraperRepository.findBySiteAndType(site, type);
    return scraperDAO;
  }

}
