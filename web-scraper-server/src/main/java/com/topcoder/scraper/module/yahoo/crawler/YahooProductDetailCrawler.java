package com.topcoder.scraper.module.yahoo.crawler;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Crawl yahoo product detail page
 */
public class YahooProductDetailCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooProductDetailCrawler.class);
  private String siteName;
  private final WebpageService webpageService;

  public YahooProductDetailCrawler(
          String siteName,
          WebpageService webpageService) {
    this.siteName = siteName;
    this.webpageService = webpageService;
  }

  /**
   *
   * Fetch product information
   * @param webClient the web client
   * @param productCode the product code
   * @param saveHtml true if product html page will be saved
   * @return YahooProductDetailCrawlerResult
   * @throws IOException
   */
  public YahooProductDetailCrawlerResult fetchProductInfo(TrafficWebClient webClient, String productCode, boolean saveHtml) throws IOException {

    String productName = productCode;
    YahooProductDetailCrawlerResult result = getProductInfoByCode(webClient,productName,saveHtml);

    return result;
  }

  private YahooProductDetailCrawlerResult getProductInfoByCode(TrafficWebClient webClient, String code, boolean saveHtml) throws IOException {

    String htmlPath = "https://store.shopping.yahoo.co.jp/"+code;
    HtmlPage page = webClient.getPage(htmlPath);
    webpageService.save("yahoo-search-result", siteName, page.getWebResponse().getContentAsString());
    ProductInfo productInfo = null;

    try {
      String shouhinCode = page.querySelector("#abuserpt > p:nth-child(3)").asText().trim();
      String prodName = page.querySelector("div.elTitle > h2:nth-child(1)").asText().trim();
      String vendorName = page.querySelector("dt.elStore > a:nth-child(1)").asText().trim();
      String prodPrice = page.querySelector(".elNum").asText().trim();
      String modelNo = null; //TODO: Scrape/find kataban (modelNo)
      productInfo = new ProductInfo();
      productInfo.setCode(shouhinCode);
      productInfo.setDistributor(vendorName);
      productInfo.setName(prodName);
      productInfo.setPrice(prodPrice.replaceAll(",", "")); // TODO: ???
      productInfo.setModelNo(modelNo);
    } catch(Exception e) {
      LOGGER.info("Could not scrape item info for product " + code + " : " + e.getMessage());
    }

    return new YahooProductDetailCrawlerResult(productInfo, htmlPath);
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
   * There are different pages from amazon... /yahoo??
   * from li tag or a table
   * @param page the product page
   * @param productCode the product code
   * @return list of category string
   */
  private List<String> fetchCategoryInfoList(HtmlPage page, String productCode) {
    System.out.println("\nPretending to fetch info list! Returning empty array!"); //TODO: Something here!

    return new ArrayList<>(); //TODO: Implement
  }

  /**
   *
   * Search and Fetch product information
   * @param webClient the web client
   * @param modelNo the mode no
   * @param saveHtml true if product html page will be saved
   * @return YahooProductDetailCrawlerResult
   * @throws IOException
   */
  public YahooProductDetailCrawlerResult searchProductAndFetchProductInfoByModelNo(TrafficWebClient webClient, String modelNo, boolean saveHtml) throws IOException  {

    String productCode = searchProduct(webClient,modelNo);
    if (productCode == null) {
      LOGGER.info(String.format("Could not find name info for model no %s",modelNo));
      return null;
    }
    return fetchProductInfo(webClient, productCode, saveHtml);
  }

  /**
   *
   * Search product
   * @param webClient the web client
   * @param  searchWord
   * @return String asin no(product code)
   * @throws IOException
   */
  private String searchProduct(TrafficWebClient webClient, String searchWord) throws IOException {
    System.out.println("\nPretending to fetch searchProduct! Returning null!"); //TODO: Implement
    return null; //TODO: Implement
  }
}
