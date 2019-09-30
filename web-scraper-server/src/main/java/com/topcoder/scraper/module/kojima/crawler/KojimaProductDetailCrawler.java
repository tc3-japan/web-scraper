package com.topcoder.scraper.module.kojima.crawler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.navpage.NavigableProductDetailPage;
import com.topcoder.scraper.module.general.ProductDetailCrawler;
import com.topcoder.scraper.module.general.ProductDetailCrawlerResult;
import com.topcoder.scraper.service.WebpageService;


public class KojimaProductDetailCrawler extends ProductDetailCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaProductDetailCrawler.class);
  private String siteName;
  private final WebpageService webpageService;

  public KojimaProductDetailCrawler(String siteName, WebpageService webpageService) {
	super(siteName, webpageService);
    this.siteName = siteName;
	this.webpageService = webpageService;
	
  }
  
  public ProductDetailCrawlerResult fetchProductInfo(TrafficWebClient webClient, String productName, boolean saveHtml) throws IOException {

    LOGGER.info("Product name " + productName);
    
	ProductInfo productInfo = new ProductInfo();

	NavigableProductDetailPage detailPage = new NavigableProductDetailPage("https://www.kojima.net/ec/top/CSfTop.jsp", webClient, productInfo);

	/*
	detailPage.type(productName, "#q");
	detailPage.click("#btnSearch");
	detailPage.setName("h1.htxt02");
	detailPage.setDistributor("span");
	detailPage.setPrice("td.price > span");
	detailPage.setQuantity(".cart_box > div:nth-child(1) > form:nth-child(1) > input:nth-child(2)");
	detailPage.setModelNo("#item_detail > div > div.item_detail_box > table > tbody > tr:nth-child(6) > td");
	*/

	//setName(productInfo, "h1.htxt02");
	//setDistributor(productInfo, "span");
	//setPrice(productInfo, "td.price > span");  
	//setModelNo(productInfo, "#item_detail > div > div.item_detail_box > table > tbody > tr:nth-child(6) > td");
	
	  
	String savedPath = null;
	if (saveHtml) {
	  savedPath = detailPage.savePage("kojima-product-details", siteName, webpageService);
	}
	ProductDetailCrawlerResult result = new ProductDetailCrawlerResult(detailPage.getProductInfo(), savedPath);

    LOGGER.info("Product name from Purchase history: [" + productName + "]");
    LOGGER.info("Product name from Product page    : [" + result.getProductInfo().getName()+ "] matched: " + (productName.equals(result.getProductInfo().getName())));

	return result;
  }

}
