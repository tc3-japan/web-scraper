package com.topcoder.scraper.command.impl;

import com.topcoder.scraper.command.AbstractCommand;
import com.topcoder.scraper.exception.ChangeDetectionFailure;
import com.topcoder.scraper.module.ChangeDetectionInitModule;
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
public class ChangeDetectionInitCommand extends AbstractCommand<ChangeDetectionInitModule> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChangeDetectionInitCommand.class);

  @Autowired
  public ChangeDetectionInitCommand(List<ChangeDetectionInitModule> modules) {
    super(modules);
  }

  /**
   * Run init method from module
   * @param module module to be run
   */
  @Override
  protected void process(ChangeDetectionInitModule module) {
    try {
      module.init();
    } catch (IOException e) {
      LOGGER.error("Fail to init check detection", e);
      throw new ChangeDetectionFailure();
    }
    LOGGER.info("Successfully init check detection");
  }
}
