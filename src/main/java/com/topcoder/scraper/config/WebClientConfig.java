package com.topcoder.scraper.config;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web Client Configuration
 */
@Configuration
public class WebClientConfig {


  @Bean
  public WebClient webClient() {
    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

    return webClient;
  }
}
