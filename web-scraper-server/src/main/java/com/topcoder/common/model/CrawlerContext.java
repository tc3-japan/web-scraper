package com.topcoder.common.model;

import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler.AmazonAuthenticationCrawler;
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
  private AmazonAuthenticationCrawler crawler;


  public CrawlerContext(TrafficWebClient webClient, AmazonProperty property, WebpageService webpageService, String uuid,
                        AmazonAuthenticationCrawler crawler) {
    this.webClient = webClient;
    this.property = property;
    this.webpageService = webpageService;
    this.uuid = uuid;
    this.crawler = crawler;
  }
}
