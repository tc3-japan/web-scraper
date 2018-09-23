package com.topcoder.scraper;

import com.topcoder.scraper.command.impl.AuthenticationCommand;
import com.topcoder.scraper.command.impl.PurchaseHistoryListCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * AppRunner is an implementation of {@link ApplicationRunner}
 */
@Component
public class AppRunner implements ApplicationRunner {

  private final AuthenticationCommand authenticationCommand;
  private final PurchaseHistoryListCommand purchaseHistoryListCommand;

  @Autowired
  public AppRunner(AuthenticationCommand authenticationCommand, PurchaseHistoryListCommand purchaseHistoryListCommand) {
    this.authenticationCommand = authenticationCommand;
    this.purchaseHistoryListCommand = purchaseHistoryListCommand;
  }

  /**
   * Create browser driver,
   * run {@link com.topcoder.scraper.command.impl.AuthenticationCommand}
   * then {@link PurchaseHistoryListCommand}
   *
   * @param args ApplicationArguments from input
   */
  @Override
  public void run(ApplicationArguments args) {
    authenticationCommand.run(args);
    purchaseHistoryListCommand.run(args);
  }

}
