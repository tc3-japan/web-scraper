package com.topcoder.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.topcoder.api.exception.ApiException;
import com.topcoder.api.service.ScraperService;
import com.topcoder.common.dao.ScraperDAO;

/**
 * rest api for scraper
 */
@RestController
@RequestMapping("/scrapers")
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
    return scraperService.getScript(site, type).getScript();
  }

  /**
   * update script by site and type
   *
   * @param site the ec site
   * @param type the logic type
   * @param entity the scraper request entity
   * @throws ApiException if any error happened
   */
  @PutMapping("/{site}/{type}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateScript(@PathVariable("site") String site, @PathVariable("type") String type, @RequestBody ScraperDAO entity) throws ApiException {
    scraperService.updateScript(site, type, entity);
  }

}
