package com.topcoder.scraper.command.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.repository.ProductRepository;
import com.topcoder.common.service.SolrService;
import com.topcoder.common.service.SolrService.SolrPorduct;
import com.topcoder.common.util.Common;
import com.topcoder.common.util.scraping.ScrapingFieldProcessor;
import com.topcoder.common.util.scraping.ScrapingFieldProperty;

// TODO : re-consider whether this class is needed or not.
@Component
public class DemoCommand {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserEncoderCommand.class);

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private SolrService solrService;

  public void run(ApplicationArguments args) {

    if (args.containsOption("similar")) {
      LOGGER.info("--similar=" + args.getOptionValues("similar").get(0));
      try {
        int productId = Integer.valueOf(args.getOptionValues("similar").get(0));
        ProductDAO product = productRepository.findById(productId);
        List<SolrPorduct> similarProds = this.solrService.searchSimilarProducts(product);
        LOGGER.info("Search similar products by:");
        LOGGER.info(Common.toJSON(product));
        LOGGER.info("# of results: " + similarProds.size());
        similarProds.forEach(p -> {
          LOGGER.info(Common.toJSON(p));
        });
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
      return;
    }

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
