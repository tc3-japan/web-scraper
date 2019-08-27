package com.topcoder.scraper.command.impl;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.util.scraping.ScrapingFieldProperty;
import com.topcoder.common.util.scraping.ScrapingFieldProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class DemoCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserEncoderCommand.class);

  public void run(ApplicationArguments args) {
    WebClient webClient = new WebClient();
    HtmlPage page = null;
    try {
      page = webClient.getPage("https://www.google.com");
    } catch (Exception e) {}

    ScrapingFieldProperty scrapingFieldProperty = new ScrapingFieldProperty("#fsl", "String", null, null);
    String value = ScrapingFieldProcessor.<String>prepare(page, scrapingFieldProperty).process();

    LOGGER.info(">>>>> [DEMO] #fsl:" + value);
  }
}
