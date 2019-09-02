package com.topcoder.scraper.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.ProductDetailCrawlerResult;
import com.topcoder.scraper.service.WebpageService;

public abstract class ProductDetailCrawler {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ProductDetailCrawler.class);
	protected String siteName;
	protected final WebpageService webpageService;
	protected HtmlPage productDetailPage;

	public ProductDetailCrawler(String siteName, WebpageService webpageService) {
		this.siteName = siteName;
		this.webpageService = webpageService;
		productDetailPage = null;
	}

	public abstract ProductDetailCrawlerResult fetchProductInfo(TrafficWebClient webClient, String productName,
			boolean saveHtml) throws IOException;

	/**
	 * Fetch category info list from webpage There are different pages from
	 * amazon... /yahoo?? from li tag or a table
	 * 
	 * @param page        the product page
	 * @param productCode the product code
	 * @return list of category string
	 */
	protected List<String> fetchCategoryInfoList(HtmlPage page, String productCode) {
		System.out.println("\nPretending to fetch info list! Returning empty array!"); // TODO: Something here!

		return new ArrayList<>(); // TODO: Implement
	}

	/**
	 * Find category ranking and save in database
	 * 
	 * @param productPage the product detail page
	 * @param info        the product info to be updated
	 * @param productCode the product code
	 */
	protected void fetchCategoryRanking(HtmlPage productPage, ProductInfo info, String productCode) {
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

}