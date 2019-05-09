package com.topcoder.scraper.module.amazon.crawler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.exception.SessionExpiredException;
import com.topcoder.scraper.service.WebpageService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import static com.topcoder.common.util.HtmlUtils.*;

/**
 * Crawl amazon product detail page
 */
public class AmazonProductDetailCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonProductDetailCrawler.class);
  private String siteName;
  private AmazonProperty property;
  private final WebpageService webpageService;

  public AmazonProductDetailCrawler(
    String siteName,
    AmazonProperty property,
    WebpageService webpageService) {
    this.siteName = siteName;
    this.property = property;
    this.webpageService = webpageService;
  }

  /**
   *
   * Fetch product information
   * @param webClient the web client
   * @param productCode the product code
   * @param saveHtml true if product html page will be saved
   * @return AmazonProductDetailCrawlerResult
   * @throws IOException
   */
  public AmazonProductDetailCrawlerResult fetchProductInfo(TrafficWebClient webClient, String productCode, boolean saveHtml) throws IOException {

    String productUrl = property.getProductUrl() + productCode;
    LOGGER.info("Product url " + productUrl);

    HtmlPage productPage = webClient.getPage(productUrl);
    /* TODO: Pending
    if (AmazonPurchaseHistoryListCrawler.isSessionExpired(productPage)) {
      throw new SessionExpiredException("Session has been expired.");
    }
    */
    ProductInfo productInfo = new ProductInfo();
    fetchProductInfo(productPage, productInfo, productCode);
    fetchCategoryRanking(productPage, productInfo, productCode);

    String savedPath = null;
    if (saveHtml) {
      savedPath = webpageService.save("product", siteName, productPage.getWebResponse().getContentAsString());
    }

    productInfo.setCode(productCode);
    return new AmazonProductDetailCrawlerResult(productInfo, savedPath);
  }

  /**
   * Update product information
   * @param productPage the product detail page
   * @param info the product info to be updated
   * @param productCode the product code
   */
  private void fetchProductInfo(HtmlPage productPage, ProductInfo info, String productCode) {

    // update price
    // Pair includes element and it's selector string.
    Pair<HtmlElement, String> priceElementPair = findFirstElementInSelectors(productPage, property.getCrawling().getProductDetailPage().getPrices());
    if (priceElementPair == null) {
      LOGGER.info(String.format("Could not find price info for product %s:%s",
        this.siteName, productCode));
    } else {
      HtmlElement priceElement  = priceElementPair.getFirst();
      String      priceSelector = priceElementPair.getSecond();
      LOGGER.info("Price's found by selector: " + priceSelector);

      String price = getTextContentWithoutDuplicatedSpaces(priceElement);

      // special case handle, for example
      // https://www.amazon.com/gp/product/B016KBVBCS
      // current value of price is $ 75 55
      String[] priceArray = price.split(" ");
      if (priceArray.length == 3) {
        price = String.format("%s%s.%s", priceArray[0], priceArray[1], priceArray[2]);
      }

      info.setPrice(getNumberAsStringFrom(price));
    }

    // update name
    HtmlElement nameElement = productPage.querySelector(property.getCrawling().getProductDetailPage().getName());
    if (nameElement == null) {
      LOGGER.info(String.format("Could not find name info for product %s:%s",
        this.siteName, productCode));
    } else {
      String name = getTextContent(nameElement);
      info.setName(name);
    }
  }
  /**
   * Find category ranking and save in database
   * @param productPage the product detail page
   * @param info the product info to be updated
   * @param productCode the product code
   */
  private void fetchCategoryRanking(HtmlPage productPage, ProductInfo info, String productCode) {
    List<String> categoryInfoList = fetchCategoryInfoList(productPage, productCode);

    for (String data : categoryInfoList) {

      // categoryInfo = [rank] [in] [category path]
      // in may contain ascii char number 160, so replace it with space
      String[] categoryInfo = data.replace("\u00A0", " ").split(" ", 3);

      // remove possible leading # and comma, then convert to int
      int rank = Integer.valueOf(categoryInfo[0].replaceAll("[^0-9]*", ""));

      // remove See [Tt]op 100 info from category path
      String path = categoryInfo[2];
      int topIndex = path.indexOf(" (See ");
      if (topIndex != -1) {
        path = path.substring(0, topIndex);
      }

      info.addCategoryRanking(path, rank);
    }

  }

  /**
   * Fetch category info list from webpage
   * There are different pages from amazon
   * from li tag or a table
   * @param page the product page
   * @param productCode the product code
   * @return list of category string
   */
  private List<String> fetchCategoryInfoList(HtmlPage page, String productCode) {
    DomNode node = page.querySelector(property.getCrawling().getProductDetailPage().getSalesRank());

    // category ranking is from li#salesrank
    if (node != null) {
      List<String> categoryInfoList = new ArrayList<>();

      // get first rank and category path
      Pattern pattern = Pattern.compile("(#.*? in .*?)\\(");
      Matcher matcher = pattern.matcher(node.getTextContent());
      if (matcher.find()) {
        String firstRankAndPath = matcher.group(1).trim();
        categoryInfoList.add(firstRankAndPath);
      }

      // get rest of ranks and category paths
      List<DomNode> ranks = node.querySelectorAll("ul > li > span:nth-of-type(1)");
      List<DomNode> paths = node.querySelectorAll("ul > li > span:nth-of-type(2)");
      for (int i = 0; i < ranks.size(); i++) {
        categoryInfoList.add(
          getTextContent((HtmlElement) ranks.get(i)) + " " + getTextContent((HtmlElement) paths.get(i)));
      }

      return categoryInfoList;
    }

    node = page.querySelector(property.getCrawling().getProductDetailPage().getProductInfoTable());

    // category ranking is from product table
    if (node != null) {
      List<DomNode> trList = node.querySelectorAll("tbody > tr");
      for (DomNode tr : trList) {
        if (getTextContent(tr.querySelector("th")).contains("Rank")) {
          List<DomNode> spanList = tr.querySelectorAll("td > span > span");
          return spanList.stream().map(span -> getTextContent((HtmlElement) span)).collect(Collectors.toList());
        }
      }
    }

    LOGGER.info(String.format("Could not find category rankings for product %s:%s",
      this.siteName, productCode));
    return new ArrayList<>();
  }
}
