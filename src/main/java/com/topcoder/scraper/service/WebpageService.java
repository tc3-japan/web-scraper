package com.topcoder.scraper.service;

import com.gargoylesoftware.htmlunit.html.HtmlImage;

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

  String saveImage(String filename, String fileExt, String site, HtmlImage htmlImage);
}
