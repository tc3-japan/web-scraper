package com.topcoder.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.topcoder.api.exception.ApiException;
import com.topcoder.api.exception.EntityNotFoundException;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.scraper.module.ecunifiedmodule.DryRunPurchaseHistoryModule;
import com.topcoder.common.dao.ConfigurationDAO;
import com.topcoder.common.repository.ConfigurationRepository;

/**
 * scraper service
 */
@Service
public class ConfigurationService {

  /**
   * the scraper Repository
   */
  @Autowired
  ConfigurationRepository configurationRepository;

  /**
   * ec site account repository
   */
  @Autowired
  ECSiteAccountRepository ecSiteAccountRepository;

  @Autowired
  DryRunPurchaseHistoryModule dryRunPurchaseHistoryModule;

  /**
   * get config by site and type
   *
   * @param site the ec site
   * @param type the logic type
   * @return the config text
   * @throws EntityNotFoundException if not found
   */
  public String getConfig(String site, String type) throws EntityNotFoundException {
	ConfigurationDAO configurationDAO = get(site, type);
    if (configurationDAO == null) {
      throw new EntityNotFoundException("Cannot found config where site = " + site + " and " + type);
    }
	return get(site, type).getConfig();
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
  public String createOrUpdateConfiguration(String site, String type, String conf) throws ApiException {
	try {
	  String resultText = "success ";

	  ConfigurationDAO configurationDAO = get(site, type);

	  if (configurationDAO == null) {
	    configurationDAO = new ConfigurationDAO();
	    resultText += "create record to scraper table";
	  } else {
	    resultText += "update record to scraper table";
	  }

      configurationDAO.setSite(site);
      configurationDAO.setType(type);
      configurationDAO.setConfig(conf);
      configurationRepository.save(configurationDAO);

      return resultText;

    } catch(Exception e) {
      e.printStackTrace();
	  throw new ApiException("failed to create or update conf");
    }
  }

  /**
   * execute conf
   *
   * @param site the ec site
   * @param type the logic type
   * @param request to executable conf
   * @throws ApiException if any error happened
   */
  public List<PurchaseHistory> executeConfiguration(String site, String type, String conf) throws ApiException {
    try {
	  dryRunPurchaseHistoryModule.setConfig(conf);

	  List<String> sites = new ArrayList<String>();
	  sites.add(site);

	  dryRunPurchaseHistoryModule.fetchPurchaseHistoryList(sites);

	  List<PurchaseHistory> list = dryRunPurchaseHistoryModule.getPurchaseHistoryList();

	  return list;
    } catch(Exception e) {
      e.printStackTrace();
	  throw new ApiException("failed to execute conf");
    }
  }

  /**
   * get ScraperDAO by site and type
   *
   * @param site the ec site
   * @param site the logic type
   * @return the ScraperDAO
   */
  public ConfigurationDAO get(String site, String type) {
	ConfigurationDAO configurationDAO = configurationRepository.findBySiteAndType(site, type);
    return configurationDAO;
  }

}
