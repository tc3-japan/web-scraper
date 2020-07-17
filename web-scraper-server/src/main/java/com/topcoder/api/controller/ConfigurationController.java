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
import com.topcoder.api.service.ConfigurationService;

/**
 * rest api for scraper
 */
@RestController
@RequestMapping("/scrapers")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ConfigurationController {

  @Autowired
  ConfigurationService configurationService;

  /**
   * get scraper logic
   *
   * @param site the ec site
   * @param type the logic type
   * @throws ApiException if any error happened
   */
  @GetMapping("/{site}/{type}")
  public String getConfig(@PathVariable("site") String site, @PathVariable("type") String type) throws ApiException {
    return configurationService.getConfig(site, type);
  }

  /**
   * create or update conf by site and type
   *
   * @param site the ec site
   * @param type the logic type
   * @param entity the scraper request entity
   * @return the result message text
   * @throws ApiException if any error happened
   */
  @PutMapping(path = "/{site}/{type}", consumes = "text/plain")
  public String createOrUpdateConfig(@PathVariable("site") String site, @PathVariable("type") String type, @RequestBody String conf) throws ApiException {
    return configurationService.createOrUpdateConfiguration(site, type, conf);
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
  public List<Object> executeConfig(@PathVariable("site") String site, @PathVariable("type") String type, @RequestBody String conf) throws ApiException {
    List<Object> list = configurationService.executeConfiguration(site, type, conf);
    if (list == null || list.size() == 0) {
      throw new ApiException("failed to execute conf");
    }
    return list;
  }

  /**
   * get the html string
   *
   * @param the html file name
   * @return the html string
   * @throws ApiException if any error happened
   */
  @GetMapping("/html/{filename}")
  public String getHtml(@PathVariable("filename") String htmlFileName) throws ApiException {
    return configurationService.getHtmlString(htmlFileName);
  }

}
