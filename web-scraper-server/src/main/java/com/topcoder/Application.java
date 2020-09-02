package com.topcoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

// TODO: consider if we set FlywayAutoConfiguration.class, or not.
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
        new SpringApplicationBuilder(Application.class).web(isRestMode).run(args);


    }

}
