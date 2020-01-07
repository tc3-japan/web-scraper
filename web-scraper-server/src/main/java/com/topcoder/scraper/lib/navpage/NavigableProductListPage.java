package com.topcoder.scraper.lib.navpage;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.HtmlUtils;

public class NavigableProductListPage extends NavigablePage {

	private static final Logger LOGGER = LoggerFactory.getLogger(NavigableProductListPage.class);

	public NavigableProductListPage(TrafficWebClient webClient) {
		super((HtmlPage)null, webClient);
		LOGGER.info("[constructor] in");
	}

	public void searchProductsUsingForm(String searchUrl, String searchFormName, String searchInputName, String searchButtonSelector, String searchWord) {
		LOGGER.info("[searchProductsUsingForm] in");

		try {
			HtmlPage topPage    = this.webClient.getWebClient().getPage(searchUrl);
			HtmlForm searchForm = topPage.getFormByName(searchFormName);

			HtmlTextInput searchInput = searchForm.getInputByName(searchInputName);
			searchInput.type(searchWord);
			HtmlImageInput searchButtonInput = topPage.querySelector(searchButtonSelector);

			this.page = webClient.click(searchButtonInput);
		} catch (IOException e) {
			LOGGER.info("[searchProductsUsingForm] failed to get products. page: " + searchUrl + "search word:" + searchWord);
		}
	}

	public String scrapeProductCodeFromSearchResultByProductAttrName(String searchWord, String searchResultSelector, String productCodeAttribute, String adProductClass, String productCodeRegex) {
		LOGGER.info("[scrapeProductCodeFromSearchResultByProductAttrName] in");

		HtmlElement element = this.page.querySelector(searchResultSelector);
		if (element == null) {
			return null;
		}
		//skip ad product
		if (StringUtils.isNotEmpty(adProductClass)) {
			if (element.getAttribute("class").contains(adProductClass)) {
				LOGGER.info(String.format("Skip ad product with search word = %s", searchWord));
				return null;
			}
		}
		// get product code (amazon: data-asin, yahoo: data-beacon)
		String productCode = element.getAttribute(productCodeAttribute);
		// extract product code using regex (yahoo)
		if (StringUtils.isNotEmpty(productCode) && StringUtils.isNotEmpty(productCodeRegex)) {
			productCode = HtmlUtils.extract1(productCode, Pattern.compile(productCodeRegex));
		}
    if (StringUtils.isEmpty(productCode)) {
			return null;
		}

		LOGGER.info(String.format("Product is found with search word = %s, product code is %s", searchWord, productCode));
		return productCode;
	}

	public String scrapeProductCodeFromSearchResultByProductUrl(String searchWord, String searchResultSelector, String productCodeRegex) {
		LOGGER.info("[scrapeProductCodeFromSearchResultByProductUrl] in");

		HtmlAnchor anchor = this.page.querySelector(searchResultSelector);
		if (anchor == null) {
			return null;
		}
		//get product code
		String hrefStr     = anchor.getHrefAttribute();
		String productCode = HtmlUtils.extract1(hrefStr, Pattern.compile(productCodeRegex));
		if(productCode == null) {
			return null;
		}
		LOGGER.info(String.format("Product is found with search word = %s, product code is %s", searchWord, productCode));
		return productCode;
	}
}
