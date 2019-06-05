package com.topcoder.scraper.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.scraper.service.PurchaseHistoryService;
import com.topcoder.common.util.DateUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File based implementation of PurchaseHistoryService
 * which all records are saved as files in disk
 */
public class FileBasedPurchaseHistoryService implements PurchaseHistoryService {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedPurchaseHistoryService.class);
  private static final ObjectMapper OB = new ObjectMapper();

  @Override
  public void save(String site, List<PurchaseHistory> list) {

    if (list.size() == 0) {
      LOGGER.info("No new purchase history");
      return;
    }

    try {
      FileUtils.write(new File(filename(site)), OB.writeValueAsString(list), "UTF-8");
    } catch (IOException e) {
      LOGGER.error("Fail to write purchase history file", e);
    }

    LOGGER.info("New purchase histories are saved");
  }

  @Override
  public List<PurchaseHistory> listAll(String site) {
    List<PurchaseHistory> histories = new ArrayList<>();

    Collection<File> jsonFiles = listFiles(site);
    for (File f : jsonFiles) {
      try {
        histories.addAll(
          OB.readValue(f, new TypeReference<List<PurchaseHistory>>(){}));
      } catch (IOException e) {
        LOGGER.error("Fail to read json file, skip");
      }
    }
    return histories;
  }
  
  @Override
  public Optional<PurchaseHistory> fetchLast(int siteId) {
    throw new UnsupportedOperationException();
  }
  

  /**
   * generate filename
   * @param site site name
   * @return absolute file path
   */
  private String filename(String site) {

    return new File("").getAbsoluteFile().getAbsolutePath() +
      File.separator +
      site +
      File.separator +
      "history-" +
      DateUtils.currentDateTime() +
      ".json";
  }

  /**
   * Finds all json files
   * @param site site name
   * @return list of json files
   */
  private Collection<File> listFiles(String site) {
    File folder = new File(
      new File("").getAbsoluteFile().getAbsolutePath() +
        File.separator +
        site
    );
    Collection<File> jsonFiles = FileUtils.listFiles(
      folder,
      new String[]{"json"},
      false
    );

    return jsonFiles;
  }

}
