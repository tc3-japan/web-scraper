package com.topcoder.common.model;

import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractAuthenticationCrawler;
import com.topcoder.scraper.service.WebpageService;
import lombok.Data;

/**
 * the Crawler context for each session/uuid
 */
@Data
public class CrawlerContext {

  /**
   * the web client
   */
  private TrafficWebClient webClient;

  // TODO: delete AmazonProperty
  /**
   * the Amazon Property
   */
  private AmazonProperty property;

  /**
   * the web page service
   */
  private WebpageService webpageService;

  /**
   * the session/task id
   */
  private String uuid;

  /**
   * the auth crawler
   */
  private AbstractAuthenticationCrawler crawler;

  // TODO: delete AmazonProperty
  public CrawlerContext(TrafficWebClient webClient, AmazonProperty property, WebpageService webpageService, String uuid,
                        AbstractAuthenticationCrawler crawler) {
    this.webClient      = webClient;
    this.property       = property;
    this.webpageService = webpageService;
    this.uuid           = uuid;
    this.crawler        = crawler;
  }
}
