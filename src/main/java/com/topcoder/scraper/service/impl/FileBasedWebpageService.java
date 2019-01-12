package com.topcoder.scraper.service.impl;

import com.topcoder.scraper.service.WebpageService;
import com.topcoder.scraper.util.DateUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * File based WebpageService
 * which all records are saved as files in disk
 */
@Service
public class FileBasedWebpageService implements WebpageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedWebpageService.class);

  @Override
  public String save(String filename, String site, String content) {
    String path = generateFilename(filename, site);
    try {
      FileUtils.write(new File(path), content, "UTF-8");
      LOGGER.info("Web page saved");
    } catch (IOException e) {
      LOGGER.error("Fail to write to webpage file");
    }
    return path;
  }

  /**
   * generate filename
   * @param site site name
   * @return absolute file path
   */
  private String generateFilename(String filename, String site) {

    return new File("").getAbsoluteFile().getAbsolutePath() +
      File.separator +
      site +
      File.separator +
      filename +
      "-" +
      DateUtils.currentDateTime() +
      ".html";
  }
}
