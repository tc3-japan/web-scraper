package com.topcoder.scraper.lib.navpage;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.HtmlUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.topcoder.common.util.HtmlUtils.extractInt;

public class NavigableProductDetailPage extends NavigablePage {

	private static final Logger LOGGER = LoggerFactory.getLogger(NavigablePurchaseHistoryPage.class);

	// TrafficWebClient webClient;
	// HtmlPage page;
	@Getter@Setter private ProductInfo productInfo;

	public NavigableProductDetailPage(HtmlPage page, TrafficWebClient webClient, ProductInfo productInfo) {
		super(page, webClient);
		LOGGER.info("[constructor] in");
		this.productInfo = productInfo;
	}

	public NavigableProductDetailPage(String url, TrafficWebClient webClient, ProductInfo productInfo) {
		super(url, webClient);
		LOGGER.info("[constructor] in");
		this.productInfo = productInfo;
	}

	public NavigableProductDetailPage(TrafficWebClient webClient) {
		super((HtmlPage)null, webClient);
		LOGGER.info("[constructor] in");
	}

	public void scrapeDistributor(String selector) {
		LOGGER.info("[scrapeDistributor] in");
		String str = getText(selector);
		LOGGER.info("[scrapeDistributor] Distributor >>>> " + str);
		if (str != null) {
			productInfo.setDistributor(str);
		}
	}

	public void scrapeDistributor(DomNode node, String selector) {
		LOGGER.info("[scrapeDistributor] in");
		String str = getText(node, selector);
		LOGGER.info("[scrapeDistributor] Distributor >>>> " + str);
		if (str != null) {
			productInfo.setDistributor(str);
		}
	}

	public void scrapeCode(String selector) {
		LOGGER.info("[scrapeCode] in");
		String code = getText(selector);
		LOGGER.info("[scrapeCode] Code >>>> " + code);
		if (code != null) {
			productInfo.setCode(code);
		}
	}

	public void scrapeCode(DomNode node, String selector) {
		LOGGER.info("[scrapeCode] in");
		String code = getText(node, selector);
		LOGGER.info("[scrapeCode] Code >>>> " + code);
		if (code != null) {
			productInfo.setCode(code);
		}
	}

	public void scrapeName(String selector) {
		LOGGER.info("[scrapeName] in");
		String str = getText(selector);
		LOGGER.info("[scrapeName] Name >>>> " + str);
		if (str != null) {
			productInfo.setName(str);
		}
	}

	public void scrapeName(DomNode node, String selector) {
		LOGGER.info("[scrapeName] in");
		String str = getText(node, selector);
		LOGGER.info("[scrapeName] Name >>>> " + str);
		if (str != null) {
			productInfo.setName(str);
		}
	}

	public void scrapePrice(String selector) {
		LOGGER.info("[scrapePrice] in");
		String str = getText(selector);
		LOGGER.info("[scrapePrice] Price >>>> " + str);
		if (str != null) {
			productInfo.setPrice(str);
		}
	}

	public void scrapePrice(DomNode node, String selector) {
		LOGGER.info("[scrapePrice] in");
		String str = getText(node, selector);
		LOGGER.info("[scrapePrice] Price >>>> " + str);
		if (str != null) {
			productInfo.setPrice(str);
		}
	}

	public void scrapePrices(List<String> selectors) {
		LOGGER.info("[scrapePrices] in");
		for (String selector : selectors) {
			String str = getText(selector);
			LOGGER.info("[scrapePrices] Price >>>> " + str);
			if (str != null) {
				productInfo.setPrice(str);
				return;
			}
		}
	}

	public void scrapeModelNo(String selector) {
		LOGGER.info("[scrapeModelNo] in");
		String str = getText(selector);
		str = str.replaceAll("[^0-9a-zA-Z\\-]", "").trim();
		LOGGER.info("[scrapeModelNo] Model No >>>> " + str);
		if (str != null) {
			productInfo.setModelNo(str);
		}
	}

	public void scrapeModelNo(DomNode node, String selector) {
		LOGGER.info("[scrapeModelNo] in");
		String str = getText(node, selector);
		str = str.replaceAll("[^0-9a-zA-Z\\-]", "").trim();
		LOGGER.info("[scrapeModelNo] Model No >>>> " + str);
		if (str != null) {
			productInfo.setModelNo(str);
		}
	}

	public void scrapeModelNo(List<String> modelNoLabelNames, List<String> modelNoLabelSelectors, List<String> modelNoValueSelectors) {
		LOGGER.info("[scrapeModelNo] in");
		HtmlElement modelNoLabelElement = null;
		HtmlElement modelNoValueElement = null;
		for(int i = 0 ; i < modelNoLabelSelectors.size() ; i++) {
			modelNoLabelElement = this.page.querySelector(modelNoLabelSelectors.get(i));
			modelNoValueElement = this.page.querySelector(modelNoValueSelectors.get(i));

			if (modelNoLabelElement != null
							&& modelNoValueElement != null
							&& HtmlUtils.getTextContent(modelNoLabelElement).replaceAll("[:：]", "").equals(modelNoLabelNames.get(i))) {

				LOGGER.info(String.format("[scrapeModelNo] model no (%s) is found by selector: %s", modelNoLabelNames.get(i), modelNoValueSelectors.get(i)));
				String modelNo = HtmlUtils.getTextContentWithoutDuplicatedSpaces(modelNoValueElement).replaceAll("[^0-9a-zA-Z\\-]", "").trim();
				this.productInfo.setModelNo(modelNo);
				break;
			}
		}
	}

	private static final String MODEL_NO_LABEL_NAME_KEY     = "label_name";
	private static final String MODEL_NO_LABEL_SELECTOR_KEY = "label_selector";
	private static final String MODEL_NO_VALUE_SELECTOR_KEY = "value_selector";
	public void scrapeModelNo(List<Map<String, String>> modelNoSelectors) {
		LOGGER.info("[scrapeModelNo] in");
		HtmlElement modelNoLabelElement = null;
		HtmlElement modelNoValueElement = null;
		for(Map<String, String> modelNoSelector : modelNoSelectors) {
			modelNoLabelElement = this.page.querySelector(modelNoSelector.get(MODEL_NO_LABEL_SELECTOR_KEY));
			modelNoValueElement = this.page.querySelector(modelNoSelector.get(MODEL_NO_VALUE_SELECTOR_KEY));

			if (modelNoLabelElement != null
							&& modelNoValueElement != null
							&& HtmlUtils.getTextContent(modelNoLabelElement).replaceAll("[:：]", "").equals(modelNoSelector.get(MODEL_NO_LABEL_NAME_KEY))) {

				LOGGER.info(String.format("[scrapeModelNo] model no (%s) is found by selector: %s", modelNoSelector.get(MODEL_NO_LABEL_NAME_KEY), modelNoSelector.get(MODEL_NO_VALUE_SELECTOR_KEY)));
				String modelNo = HtmlUtils.getTextContentWithoutDuplicatedSpaces(modelNoValueElement).replaceAll("[^0-9a-zA-Z\\-]", "").trim();
				this.productInfo.setModelNo(modelNo);
				break;
			}
		}
	}

	public void scrapeQuantity(String selector) {
		LOGGER.info("[scrapeQuantity] in");
		String str = getText(selector);
		LOGGER.info("[scrapeQuantity] Quantity >>>> " + str);
		Integer qty = extractInt(str);
		if (str != null && qty != null) {
			// productInfo.setQuantity(extractInt(str));
			productInfo.setQuantity(qty);
		}
	}

	public void scrapeQuantity(DomNode node, String selector) {
		LOGGER.info("[scrapeQuantity] in");
		String str = getText(node, selector);
		LOGGER.info("[scrapeQuantity] Quantity >>>> " + str);
		if (str != null) {
			productInfo.setQuantity(extractInt(str));
		}
	}
}
