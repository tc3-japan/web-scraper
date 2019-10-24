package com.topcoder.scraper.command.impl;

import com.topcoder.scraper.Consts;
import com.topcoder.scraper.command.AbstractCommand;
import com.topcoder.scraper.exception.FetchPurchaseHistoryListException;
import com.topcoder.scraper.module.IPurchaseHistoryListModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Purchase History list command
 */
@Component
public class PurchaseHistoryListCommand extends AbstractCommand<IPurchaseHistoryListModule> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseHistoryListCommand.class);

  @Autowired
  public PurchaseHistoryListCommand(List<IPurchaseHistoryListModule> modules) {
    super(modules);
  }

  /**
   * fetch purchase history list from specific module
   * @param module module to be run
   */
  @Override
  protected void process(IPurchaseHistoryListModule module) {
    System.out.println("module = " + module);
    try {
      if (this.sites.size() == 0) {
        module.fetchPurchaseHistoryList(Consts.ALL_SITES);
      } else {
        module.fetchPurchaseHistoryList(this.sites);
      }
    } catch (IOException e) {
      LOGGER.error("Fail to fetch purchase history list", e);
      throw new FetchPurchaseHistoryListException();
    }
    LOGGER.info("Successfully fetch purchase history");
  }

}
