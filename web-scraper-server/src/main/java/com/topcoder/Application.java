package com.topcoder;

import com.topcoder.common.util.Common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

// TODO: consider if we set FlywayAutoConfiguration.class, or not.
@EnableAsync
@SpringBootApplication(exclude = {SolrAutoConfiguration.class})
//@SpringBootApplication(exclude = { FlywayAutoConfiguration.class, SolrAutoConfiguration.class })
//Hack: Workaround for unable to component-scan "com.topcoder.common.util.SpringTool" by "java" command.
//      Using "gradle bootRun", it can be scanned normally.
@ComponentScan({
        "com.topcoder.api",
        "com.topcoder.common",
        "com.topcoder.scraper"
})
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) {

        boolean isRestMode = false;
        for (String arg : args) {
            logger.info("arg = " + arg);
            if ("--rest".equalsIgnoreCase(arg)) {
                isRestMode = true;
            }
        }

        try {
            new SpringApplicationBuilder(Application.class).web(isRestMode).run(args);
        }
        catch (Exception e)  {
            Common.ZabbixLog(logger, e);
            throw e;
        }
    }

}
