package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import com.topcoder.common.traffic.TrafficWebClient;

import java.io.IOException;

public abstract class AbstractAuthenticationCrawler {

  public abstract AbstractAuthenticationCrawlerResult authenticate(
          TrafficWebClient webClient,
          String username,
          String password,
          String code,
          boolean init
  )throws IOException;

  public abstract AbstractAuthenticationCrawlerResult authenticate(
          TrafficWebClient webClient,
          String username,
          String password
  ) throws IOException;
}
