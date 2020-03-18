package com.topcoder.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.topcoder.api.exception.ApiException;
import com.topcoder.api.service.ScraperService;
import com.topcoder.common.dao.ScraperDAO;
import com.topcoder.common.model.ScraperRequest;
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
  @PutMapping("/{site}/{type}")
  public String createOrUpdateScript(@PathVariable("site") String site, @PathVariable("type") String type, @RequestBody ScraperDAO entity) throws ApiException {
    return scraperService.createOrUpdateScript(site, type, entity);
  }

  /**
   * search products
   *
   * @param site the ec site
   * @param type the logic type
   * @return scraping result
   * @throws ApiException if any error happened
   */
  @PostMapping("/{site}/{type}/test")
  public List<PurchaseHistory> executeScript(@PathVariable("site") String site, @PathVariable("type") String type, @RequestBody ScraperRequest request) throws ApiException {
	List<PurchaseHistory> list = scraperService.executeScript(site, type, request);
	if (list == null || list.size() == 0) {
	  throw new ApiException("scraped purchase history not found");
    }
    return list;
  }

}
