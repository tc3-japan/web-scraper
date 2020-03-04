package com.topcoder.api.controller;

import com.topcoder.api.exception.ApiException;
import com.topcoder.api.service.ScraperService;
import com.topcoder.common.dao.ScraperDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
   * @param type the logic
   * @throws ApiException if any error happened
   */
  @GetMapping("/{site}/{type}")
  public String getScript(@PathVariable("site") String site, @PathVariable("type") String type) throws ApiException {
    return scraperService.getScript(site, type).getScript();
  }

}
