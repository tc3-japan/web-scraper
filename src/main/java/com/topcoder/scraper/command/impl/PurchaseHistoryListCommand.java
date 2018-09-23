package com.topcoder.scraper.command.impl;

import com.topcoder.scraper.command.AbstractCommand;
import com.topcoder.scraper.exception.FetchPurchaseHistoryListFailure;
import com.topcoder.scraper.module.PurchaseHistoryListModule;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Purchase History list command
 */
@Component
public class PurchaseHistoryListCommand extends AbstractCommand<PurchaseHistoryListModule> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseHistoryListCommand.class);

  @Autowired
  public PurchaseHistoryListCommand(List<PurchaseHistoryListModule> modules) {
    super(modules);
  }

  /**
   * fetch purchase history list from specific module
   * @param module module to be run
   */
  @Override
  protected void process(PurchaseHistoryListModule module) {
    try {
      module.fetchPurchaseHistoryList();
    } catch (IOException e) {
      LOGGER.error("Fail to fetch purchase history list", e);
      throw new FetchPurchaseHistoryListFailure();
    }
    LOGGER.info("Successfully fetch purchase history");
  }

}
