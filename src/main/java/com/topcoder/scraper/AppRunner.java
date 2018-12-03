package com.topcoder.scraper;

import com.topcoder.scraper.command.impl.AuthenticationCommand;
import com.topcoder.scraper.command.impl.ProductDetailCommand;
import com.topcoder.scraper.command.impl.PurchaseHistoryListCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AppRunner is an implementation of {@link ApplicationRunner}
 */
@Component
public class AppRunner implements ApplicationRunner {

  private static Logger LOGGER = LoggerFactory.getLogger(AppRunner.class);

  private final AuthenticationCommand authenticationCommand;
  private final PurchaseHistoryListCommand purchaseHistoryListCommand;
  private final ProductDetailCommand productDetailCommand;

  @Autowired
  public AppRunner(AuthenticationCommand authenticationCommand,
                   PurchaseHistoryListCommand purchaseHistoryListCommand,
                   ProductDetailCommand productDetailCommand) {
    this.authenticationCommand = authenticationCommand;
    this.purchaseHistoryListCommand = purchaseHistoryListCommand;
    this.productDetailCommand = productDetailCommand;
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
    List<String> batches = args.getOptionValues("batch");

    if (batches == null) {
      usage();
      return;
    }

    if (batches.contains("purchase_history")) {
      authenticationCommand.run(args);
      purchaseHistoryListCommand.run(args);
    } else if (batches.contains("product")) {
      productDetailCommand.run(args);
    } else {
      usage();
    }
  }

  private void usage() {
    LOGGER.info("\njava -jar web-scraper.jar --batch=[purchase_history|product] --site=amazon\n    --batch is required.\n    --site is optional.");
  }

}
