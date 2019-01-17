package com.topcoder.scraper.service;

/**
 * Interface for webpage
 */
public interface WebpageService {
  /**
   * Save webpage
   * @param filename filename
   * @param site site name
   * @param content webpage content
   */
  String save(String filename, String site, String content);
}
