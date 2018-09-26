package com.topcoder.scraper.command.impl;

import com.topcoder.scraper.command.AbstractCommand;
import com.topcoder.scraper.exception.AuthenticationFailure;
import com.topcoder.scraper.module.AuthenticationModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Authentication Command
 */
@Component
public class AuthenticationCommand extends AbstractCommand<AuthenticationModule> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationCommand.class);

  @Autowired
  public AuthenticationCommand(List<AuthenticationModule> authenticationModules) {
    super(authenticationModules);
  }

  /**
   * Run authenticate from module
   * @param module module to be run
   */
  @Override
  protected void process(AuthenticationModule module) {
    try {
      module.authenticate();
    } catch (IOException e) {
      LOGGER.error("Fail to authenticate", e);
      throw new AuthenticationFailure();
    }
    LOGGER.info("Successfully authenticated");
  }
}
