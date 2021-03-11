package com.topcoder.scraper;

import java.io.IOException;
import java.util.List;

import com.topcoder.scraper.module.ecunifiedmodule.TestScrapingModule;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.topcoder.scraper.command.impl.BuildProductIndexCommand;
import com.topcoder.scraper.command.impl.ChangeDetectionCheckCommand;
import com.topcoder.scraper.command.impl.ChangeDetectionInitCommand;
import com.topcoder.scraper.command.impl.DemoCommand;
import com.topcoder.scraper.command.impl.GroupECProductsCommand;
import com.topcoder.scraper.command.impl.LoginCheckCommand;
import com.topcoder.scraper.command.impl.ProductDetailCommand;
import com.topcoder.scraper.command.impl.PurchaseHistoryListCommand;
import com.topcoder.scraper.command.impl.UserEncoderCommand;

/**
 * AppRunner is an implementation of {@link ApplicationRunner}
 */
@Component
public class AppRunner implements ApplicationRunner {

    private static Logger LOGGER = LoggerFactory.getLogger(AppRunner.class);

    private final PurchaseHistoryListCommand purchaseHistoryListCommand;
    private final ProductDetailCommand productDetailCommand;
    private final ChangeDetectionInitCommand changeDetectionInitCommand;
    private final ChangeDetectionCheckCommand changeDetectionCheckCommand;
    private final UserEncoderCommand userEncoderCommand;
    private final DemoCommand demoCommand;
    private final GroupECProductsCommand groupECProductsCommand;
    private final BuildProductIndexCommand buildProductIndexCommand;
    private final LoginCheckCommand loginCheckCommand;
    private final TestScrapingModule testScrapingModule;

    @Autowired
    public AppRunner(PurchaseHistoryListCommand purchaseHistoryListCommand, ProductDetailCommand productDetailCommand,
                     ChangeDetectionInitCommand changeDetectionInitCommand, ChangeDetectionCheckCommand changeDetectionCheckCommand,
                     UserEncoderCommand userEncoderCommand, DemoCommand demoCommand,
                     GroupECProductsCommand groupECProductsCommand, BuildProductIndexCommand buildProductIndexCommand,
                     LoginCheckCommand loginCheckCommand, TestScrapingModule testScrapingModule) {
        this.purchaseHistoryListCommand = purchaseHistoryListCommand;
        this.productDetailCommand = productDetailCommand;
        this.changeDetectionInitCommand = changeDetectionInitCommand;
        this.changeDetectionCheckCommand = changeDetectionCheckCommand;
        this.userEncoderCommand = userEncoderCommand;
        this.demoCommand = demoCommand;
        this.groupECProductsCommand = groupECProductsCommand;
        this.buildProductIndexCommand = buildProductIndexCommand;
        this.loginCheckCommand = loginCheckCommand;
        this.testScrapingModule = testScrapingModule;
    }

    /**
     * Run different command based on batch args
     *
     * @param args ApplicationArguments from input
     */
    @Override
    public void run(ApplicationArguments args) throws IOException, SolrServerException {
        List<String> batches = args.getOptionValues("batch");
        if (batches == null) {
            usage();
            return;
        }

        if (batches.contains("purchase_history")) {
            purchaseHistoryListCommand.run(args);
        } else if (batches.contains("product")) {
            productDetailCommand.run(args);
        } else if (batches.contains("change_detection_init")) {
            changeDetectionInitCommand.run(args);
        } else if (batches.contains("change_detection_check")) {
            changeDetectionCheckCommand.run(args);
        } else if (batches.contains("encrypt_user")) {
            userEncoderCommand.run(args);
        } else if (batches.contains("demo")) {
            demoCommand.run(args);
        } else if (batches.contains("group_products")) {
            groupECProductsCommand.run(args);
        } else if (batches.contains("load_product_index")) {
            buildProductIndexCommand.run(args);
        } else if (batches.contains("login_check")) {
            loginCheckCommand.run(args);
        } else if (batches.contains("test")) {
            testScrapingModule.run(args);
        } else {
            usage();
        }
    }

    private void usage() {
        LOGGER.info("\njava -jar web-scraper.jar --batch=[purchase_history|product] --site=amazon --module=unified\n    --batch is required.\n    --site is optional.\\n    --module is optional.");
    }

}
