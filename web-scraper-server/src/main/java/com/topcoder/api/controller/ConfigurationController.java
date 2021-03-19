package com.topcoder.api.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * @param site   the ec site
     * @param type   the logic type
     * @param entity the scraper request entity
     * @return the result message text
     * @throws ApiException if any error happened
     */
    @PutMapping(path = "/{site}/{type}", consumes = "text/plain")
    public String createOrUpdateConfig(@PathVariable("site") String site, @PathVariable("type") String type, @RequestBody String conf) throws IOException {
        String result = configurationService.createOrUpdateConfiguration(site, type, conf);
        configurationService.executeChangeDetectionInit(site, type);
        return result;
    }

    /**
     * execute conf as dry run
     *
     * @param site the ec site
     * @param type the logic type
     * @return scraping result
     * @throws ApiException if any error happened
     */
    @PostMapping(path = "/{site}/{type}/{count}/test", consumes = "text/plain")
    public List<Object> executeConfig(@PathVariable("site") String site,
                                      @PathVariable("type") String type,
                                      @PathVariable("count") Integer count,
                                      @RequestBody String conf) throws ApiException, IOException {
        List<Object> list = configurationService.executeConfiguration(site, type, conf, count);
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
    public String getHtml(@PathVariable("filename") String htmlFileName) throws IOException {
        return configurationService.getHtmlString(htmlFileName);
    }

}
