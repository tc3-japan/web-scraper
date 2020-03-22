package com.topcoder.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.topcoder.api.exception.ApiException;
import com.topcoder.api.service.ScraperService;
import com.topcoder.common.model.PurchaseHistory;

/**
 * rest api for scraper
 */
@RestController
@RequestMapping("/scrapers")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ScraperController {

  @Autowired
  ScraperService scraperService;

  /**
   * get scraper logic
   *
   * @param site the ec site
   * @param type the logic type
   * @throws ApiException if any error happened
   */
  @GetMapping("/{site}/{type}")
  public String getScript(@PathVariable("site") String site, @PathVariable("type") String type) throws ApiException {
    return scraperService.getScript(site, type);
  }

  /**
   * create or update script by site and type
   *
   * @param site the ec site
   * @param type the logic type
   * @param entity the scraper request entity
   * @return the result message text
   * @throws ApiException if any error happened
   */
  @PutMapping(path = "/{site}/{type}", consumes = "text/plain")
  public String createOrUpdateScript(@PathVariable("site") String site, @PathVariable("type") String type, @RequestBody String script) throws ApiException {
    return scraperService.createOrUpdateScript(site, type, script);
  }

  /**
   * search products
   *
   * @param site the ec site
   * @param type the logic type
   * @return scraping result
   * @throws ApiException if any error happened
   */
  @PostMapping(path = "/{site}/{type}/test", consumes = "text/plain")
  public List<PurchaseHistory> executeScript(@PathVariable("site") String site, @PathVariable("type") String type, @RequestBody String script) throws ApiException {
	List<PurchaseHistory> list = scraperService.executeScript(site, type, script);
	if (list == null || list.size() == 0) {
	  throw new ApiException("scraped purchase history not found");
    }
    return list;
  }

}
