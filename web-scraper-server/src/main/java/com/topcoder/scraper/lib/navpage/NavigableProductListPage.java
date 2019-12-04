package com.topcoder.scraper.lib.navpage;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.traffic.TrafficWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigableProductListPage extends NavigablePage {

	private static final Logger LOGGER = LoggerFactory.getLogger(NavigableProductListPage.class);

	public NavigableProductListPage(TrafficWebClient webClient) {
		super((HtmlPage)null, webClient);
		LOGGER.info("[constructor] in");
	}

	public String scrapeProductCodeFromSearchResult(String searchResultSelector, String productCodeAttribute, String adProductClass, String searchWord) {

		HtmlElement element = this.page.querySelector(searchResultSelector);
		if (element == null) {
			return null;
		}
		//skip ad product
		if(element.getAttribute("class").contains(adProductClass)) {
			LOGGER.info(String.format("Skip ad product with search word = %s", searchWord));
			return null;
		}
		//get asin no
		String productCode = element.getAttribute(productCodeAttribute);
		if(productCode == null) {
			return null;
		}
		LOGGER.info(String.format("Product is found with search word = %s, product code is %s", searchWord, productCode));
		return productCode;
	}
}
