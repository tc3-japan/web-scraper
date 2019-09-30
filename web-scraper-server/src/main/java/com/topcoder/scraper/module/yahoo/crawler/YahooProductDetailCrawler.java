package com.topcoder.scraper.module.yahoo.crawler;

import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.navpage.NavigableProductDetailPage;
import com.topcoder.scraper.module.general.ProductDetailCrawler;
import com.topcoder.scraper.module.general.ProductDetailCrawlerResult;
import com.topcoder.scraper.service.WebpageService;
import java.io.IOException;

/**
 * Crawl yahoo product detail page
 */
public class YahooProductDetailCrawler extends ProductDetailCrawler {

  public YahooProductDetailCrawler(String siteName, WebpageService webpageService) {
    super(siteName, webpageService);
  }

  /**
   *
   * Fetch product information
   * 
   * @param webClient   the web client
   * @param productCode the product code
   * @param saveHtml    true if product html page will be saved
   * @return ProductDetailCrawlerResult
   * @throws IOException
   */

  public ProductDetailCrawlerResult fetchProductInfo(TrafficWebClient webClient, String productCode, boolean saveHtml)
      throws IOException {
        
    ProductInfo productInfo = new ProductInfo();
    String htmlPath = "https://store.shopping.yahoo.co.jp/" + productCode;
    System.out.println(" >>> Requesting Page >>> " + htmlPath);
    NavigableProductDetailPage detailPage = new NavigableProductDetailPage(htmlPath, webClient, productInfo);

    detailPage.setCode("#abuserpt > p:nth-child(3)");
    detailPage.setName("div.elTitle > h2:nth-child(1)");
    detailPage.setDistributor("dt.elStore > a:nth-child(1)");
    detailPage.setPrice(".elNum");

    //setModelNo(productInfo, null); // TODO: Scrape/find kataban (modelNo)
    String savedPath = null;
    if (saveHtml) {
      savedPath = detailPage.savePage("kojima-product-details", siteName, webpageService);
    }

    return new ProductDetailCrawlerResult(detailPage.getProductInfo(), savedPath);
  }

  /**
   * Search product
   * 
   * @param webClient the web client
   * @param search    word
   * @return String asin no(product code)
   * @throws IOException
   */
  private String searchProduct(TrafficWebClient webClient, String searchWord) throws IOException {
    System.out.println("\nPretending to searchProduct! Returning null!"); // TODO: Implement
    return null; // TODO: Implement
  }
}
