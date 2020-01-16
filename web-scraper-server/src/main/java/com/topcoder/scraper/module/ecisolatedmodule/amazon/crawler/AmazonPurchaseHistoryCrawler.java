package com.topcoder.scraper.module.ecisolatedmodule.amazon.crawler;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ecisolatedmodule.crawler.AbstractPurchaseHistoryCrawler;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Amazon implementation of PurchaseHistoryCrawler
 */
@Component
public class AmazonPurchaseHistoryCrawler extends AbstractPurchaseHistoryCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonPurchaseHistoryCrawler.class);

  // TODO : delete, this is for PoC Code that limits the count to go to next page to 3
  protected int nextPageCount;

  @Autowired
  public AmazonPurchaseHistoryCrawler(WebpageService webpageService) {
    super("amazon", webpageService);
  }

  @Override
  public String getScriptSupportClassName() {
    return AmazonPurchaseHistoryCrawlerScriptSupport.class.getName();
  }

  @Override
  protected HtmlPage gotoNextPage(HtmlPage page, TrafficWebClient webClient) throws IOException {
    LOGGER.debug("[gotoNextPage] in");

    HtmlSelect select = page.querySelector("#orderFilter");
    // XPath Version query
    //String xxx      = page.getFirstByXPath(property.getCrawling().getPurchaseHistoryListPage().getXXX());

    String optionValue = "";
    String optionLabel = "";
    if (select != null) {
      if (select.getSelectedIndex() + 1 < select.getOptionSize()) {
        optionValue = select.getOption(select.getSelectedIndex() + 1).getValueAttribute();
      }
    }

    HtmlPage nextPage = null;
    // Try to click next page first
    HtmlAnchor nextPageAnchor = page.querySelector("#ordersContainer > div.a-row > div > ul > li.a-last > a");
    if (nextPageAnchor != null) {
      // TODO : delete below condition, this is for PoC Code that limits the count to go to next page to 3
      if (nextPageCount >= 3) {
        LOGGER.info(">>> next page count limit exceeded.");

      } else {
        LOGGER.info(">>> next page count = " + nextPageCount);
        nextPageCount++;

        LOGGER.info("goto Next Page");

        // "click" doesn't work
        //nextPage = webClient.click(nextPageAnchor);
        // below code is work-around: use "getPage" instead of "click"
        String link = "https://www.amazon.co.jp/" + nextPageAnchor.getHrefAttribute();
        nextPage = webClient.getPage(link);
        webpageService.save("purchase-history_" + optionValue, siteName, nextPage.getWebResponse().getContentAsString(), this.saveHtml);
        return nextPage;
      }
    }
    // TODO : delete, this is for PoC Code that limits the count to go to next page to 3
    nextPageCount = 0;

    // if pagination reaches end, try to go next time period
    if (select != null) {
      if (select.getSelectedIndex() + 1 < select.getOptionSize()) {
        optionValue = select.getOption(select.getSelectedIndex() + 1).getValueAttribute();
        optionLabel = select.getOption(select.getSelectedIndex() + 1).getText();
        // TODO: delete, this is for PoC Code that limits the purchase history to the one after 2018
        if (optionValue.startsWith("2018") || optionLabel.startsWith("2018")) {
          LOGGER.info("in dev: at 2018, return null and quit");
          return null;
        }
        LOGGER.info("goto " + optionLabel + ":" + optionValue + " Order Page");

        nextPage = webClient.getPage("https://www.amazon.co.jp/gp/your-account/order-history?opt=ab&digitalOrders=1&unifiedOrders=1&returnTo=&orderFilter=" + optionValue);
        webpageService.save("purchase-history_" + optionValue, siteName, nextPage.getWebResponse().getContentAsString(), this.saveHtml);
        return nextPage;
      }
    }

    return null;
  }

}
