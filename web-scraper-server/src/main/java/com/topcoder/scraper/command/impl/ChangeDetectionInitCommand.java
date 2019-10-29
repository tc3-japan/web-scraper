package com.topcoder.scraper.command.impl;

import com.topcoder.scraper.Consts;
import com.topcoder.scraper.command.AbstractCommand;
import com.topcoder.scraper.exception.ChangeDetectionException;
import com.topcoder.scraper.module.IChangeDetectionInitModule;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Change detection init Command
 */
@Component
public class ChangeDetectionInitCommand extends AbstractCommand<IChangeDetectionInitModule> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChangeDetectionInitCommand.class);

  @Autowired
  public ChangeDetectionInitCommand(List<IChangeDetectionInitModule> modules) {
    super(modules);
  }

  /**
   * Run init method from module
   * @param module module to be run
   */
  @Override
  protected void process(IChangeDetectionInitModule module) {
    try {
      if (this.sites == null || this.sites.size() == 0) {
        module.init(Consts.ALL_SITES);
      } else {
        module.init(this.sites);
      }
    } catch (IOException e) {
      LOGGER.error("Fail to init check detection", e);
      throw new ChangeDetectionException();
    }
    LOGGER.info("Successfully init check detection");
  }
}
