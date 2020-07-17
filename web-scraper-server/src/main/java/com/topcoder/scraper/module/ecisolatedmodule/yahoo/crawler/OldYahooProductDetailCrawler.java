package com.topcoder.scraper.module.ecisolatedmodule.yahoo.crawler;

import java.io.IOException;

import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.lib.navpage.NavigableProductDetailPage;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductCrawler;
import com.topcoder.scraper.module.ecunifiedmodule.crawler.GeneralProductCrawlerResult;
import com.topcoder.scraper.service.WebpageService;

/**
 * Crawl yahoo product detail page
 */
public class OldYahooProductDetailCrawler extends GeneralProductCrawler {

  public OldYahooProductDetailCrawler(String siteName, WebpageService webpageService) {
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

  public GeneralProductCrawlerResult fetchProductInfo(TrafficWebClient webClient, String productCode, boolean saveHtml)
      throws IOException {

    ProductInfo productInfo = new ProductInfo();
    String htmlPath = "https://store.shopping.yahoo.co.jp/" + productCode;
    System.out.println(" >>> Requesting Page >>> " + htmlPath);
    NavigableProductDetailPage detailPage = new NavigableProductDetailPage(htmlPath, webClient, productInfo);

    /*
    detailPage.setCode("#abuserpt > p:nth-child(3)");
    detailPage.setName("div.elTitle > h2:nth-child(1)");
    detailPage.setDistributor("dt.elStore > a:nth-child(1)");
    detailPage.setPrice(".elNum");
    */

    //setModelNo(productInfo, null); // TODO: Scrape/find kataban (modelNo)
    String savedPath = null;
    if (saveHtml) {
      //savedPath = detailPage.savePage("kojima-product-details", siteName, webpageService);
    }

    return new GeneralProductCrawlerResult(detailPage.getProductInfo(), savedPath);
  }

  /**
   * Search product
   *
   * @param webClient the web client
   * @param searchWord search word
   * @return String asin no(product code)
   * @throws IOException
   */
  @Override
  public GeneralProductCrawlerResult searchProduct(TrafficWebClient webClient, String searchWord) throws IOException {
    System.out.println("\nPretending to searchProduct! Returning null!"); // TODO: Implement
    return null; // TODO: Implement
  }

}
