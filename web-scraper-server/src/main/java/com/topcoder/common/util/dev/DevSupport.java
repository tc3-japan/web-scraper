package com.topcoder.common.util.dev;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevSupport {

  private static final Logger LOGGER = LoggerFactory.getLogger(DevSupport.class);

  public static WebClient getWebClient() {
    WebClient webClient = new WebClient(new BrowserVersion.BrowserVersionBuilder(BrowserVersion.CHROME).build());
    webClient.getOptions().setJavaScriptEnabled(false);
    return webClient;
  }

  public static void enableJs(WebClient webClient) {
    webClient.getOptions().setJavaScriptEnabled(true);
  }
  public static void disableJs(WebClient webClient) {
    webClient.getOptions().setJavaScriptEnabled(false);
  }
}
