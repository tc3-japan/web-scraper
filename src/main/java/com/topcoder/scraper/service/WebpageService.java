package com.topcoder.scraper.service;

/**
 * Interface for webpage
 */
public interface WebpageService {
  /**
   * Save webpage
   * @param site site name
   * @param content webpage content
   */
  void save(String site, String content);
}
