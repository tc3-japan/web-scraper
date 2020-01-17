package com.topcoder.scraper.lib.navpage;

import static com.topcoder.common.util.HtmlUtils.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.Common;
import com.topcoder.common.util.HtmlUtils;

import lombok.Getter;
import lombok.Setter;

public class NavigableProductDetailPage extends NavigablePage {

	private static final Logger LOGGER = LoggerFactory.getLogger(NavigableProductDetailPage.class);

	// TrafficWebClient webClient;
	// HtmlPage page;
	@Getter@Setter private ProductInfo productInfo;

	public NavigableProductDetailPage(HtmlPage page, TrafficWebClient webClient, ProductInfo productInfo) {
		super(page, webClient);
		LOGGER.debug("[constructor] in");
		this.productInfo = productInfo;
	}

	public NavigableProductDetailPage(String url, TrafficWebClient webClient, ProductInfo productInfo) {
		super(url, webClient);
		LOGGER.debug("[constructor] in");
		this.productInfo = productInfo;
	}

	public NavigableProductDetailPage(TrafficWebClient webClient) {
		super((HtmlPage)null, webClient);
		LOGGER.debug("[constructor] in");
	}

  public String scrapeText(String selector) {
    LOGGER.debug("[scrapeText] in");
    String str = getText(selector);
    LOGGER.debug("[scrapeText] text >>>> " + str);
    return str;
  }

	public void scrapeDistributor(String selector) {
		LOGGER.debug("[scrapeDistributor] in");
		String str = getText(selector);
		LOGGER.debug("[scrapeDistributor] Distributor >>>> " + str);
		if (str != null) {
			productInfo.setDistributor(str);
		}
	}

	public void scrapeDistributor(DomNode node, String selector) {
		LOGGER.debug("[scrapeDistributor] in");
		String str = getText(node, selector);
		LOGGER.debug("[scrapeDistributor] Distributor >>>> " + str);
		if (str != null) {
			productInfo.setDistributor(str);
		}
	}

	public void scrapeCode(String selector) {
		LOGGER.debug("[scrapeCode] in");
		String code = getText(selector);
		LOGGER.debug("[scrapeCode] Code >>>> " + code);
		if (code != null) {
			productInfo.setCode(code);
		}
	}

	public void scrapeCode(DomNode node, String selector) {
		LOGGER.debug("[scrapeCode] in");
		String code = getText(node, selector);
		LOGGER.debug("[scrapeCode] Code >>>> " + code);
		if (code != null) {
			productInfo.setCode(code);
		}
	}

	public void scrapeCodeFromAttr(String selector, String attrName, String codeRegexStr) {
		LOGGER.debug("[scrapeCodeFromAttr] in");
		HtmlElement node = this.page.querySelector(selector);
		String attrValue = node.getAttribute(attrName);
		Pattern pattern  = Pattern.compile(codeRegexStr);

		String str = extract1(attrValue, pattern);
		LOGGER.debug("[scrapeCodeFromAttr] >>> Setting Product Code >>>" + str);
		if (str != null) {
			productInfo.setCode(str);
		}
	}

	public void scrapeName(String selector) {
		LOGGER.debug("[scrapeName] in");
		String str = getText(selector);
		LOGGER.debug("[scrapeName] Name >>>> " + str);
		if (str != null) {
			productInfo.setName(str);
		}
	}

	public void scrapeName(DomNode node, String selector) {
		LOGGER.debug("[scrapeName] in");
		String str = getText(node, selector);
		LOGGER.debug("[scrapeName] Name >>>> " + str);
		if (str != null) {
			productInfo.setName(str);
		}
	}

	public void scrapePrice(String selector) {
		LOGGER.debug("[scrapePrice] in");
		String str = getText(selector);
		LOGGER.debug("[scrapePrice] Price >>>> " + str);
		if (str != null) {
			productInfo.setPrice(str);
		}
	}

	public void scrapePrice(DomNode node, String selector) {
		LOGGER.debug("[scrapePrice] in");
		String str = getText(node, selector);
		LOGGER.debug("[scrapePrice] Price >>>> " + str);
		if (str != null) {
			productInfo.setPrice(str);
		}
	}

	public void scrapePrices(List<String> selectors) {
		LOGGER.debug("[scrapePrices] in");
		for (String selector : selectors) {
			String str = getText(selector);
			LOGGER.debug("[scrapePrices] Price >>>> " + str);
			if (str != null) {
				productInfo.setPrice(str);
				return;
			}
		}
	}

	public void scrapeModelNo(String selector) {
		LOGGER.debug("[scrapeModelNo] in");
    String str = Common.normalize(getText(selector));
		LOGGER.debug("[scrapeModelNo] Model No >>>> " + str);
		if (str != null) {
			productInfo.setModelNo(str);
		}
	}

	public void scrapeModelNo(DomNode node, String selector) {
		LOGGER.debug("[scrapeModelNo] in");
    String str = Common.normalize(getText(node, selector));
		LOGGER.debug("[scrapeModelNo] Model No >>>> " + str);
		if (str != null) {
			productInfo.setModelNo(str);
		}
	}

	public void scrapeModelNo(List<String> modelNoLabelNames, List<String> modelNoLabelSelectors, List<String> modelNoValueSelectors) {
		LOGGER.debug("[scrapeModelNo] in");
		HtmlElement modelNoLabelElement = null;
		HtmlElement modelNoValueElement = null;
		for(int i = 0 ; i < modelNoLabelSelectors.size() ; i++) {
			modelNoLabelElement = this.page.querySelector(modelNoLabelSelectors.get(i));
			modelNoValueElement = this.page.querySelector(modelNoValueSelectors.get(i));

			if (modelNoLabelElement != null
							&& modelNoValueElement != null
							&& HtmlUtils.getTextContent(modelNoLabelElement).replaceAll("[:：]", "").equals(modelNoLabelNames.get(i))) {

				LOGGER.info(String.format("[scrapeModelNo] model no (%s) is found by selector: %s", modelNoLabelNames.get(i), modelNoValueSelectors.get(i)));
        String modelNo = Common.normalize(HtmlUtils.getTextContentWithoutDuplicatedSpaces(modelNoValueElement));
				this.productInfo.setModelNo(modelNo);
				break;
			}
		}
	}

	private static final String MODEL_NO_LABEL_NAME_KEY     = "label_name";
	private static final String MODEL_NO_LABEL_SELECTOR_KEY = "label_selector";
	private static final String MODEL_NO_VALUE_SELECTOR_KEY = "value_selector";
	public void scrapeModelNo(List<Map<String, String>> modelNoSelectors) {
		LOGGER.debug("[scrapeModelNo] in");
		HtmlElement modelNoLabelElement = null;
		HtmlElement modelNoValueElement = null;
		for(Map<String, String> modelNoSelector : modelNoSelectors) {
			modelNoLabelElement = this.page.querySelector(modelNoSelector.get(MODEL_NO_LABEL_SELECTOR_KEY));
			modelNoValueElement = this.page.querySelector(modelNoSelector.get(MODEL_NO_VALUE_SELECTOR_KEY));

			if (modelNoLabelElement != null
							&& modelNoValueElement != null
							&& HtmlUtils.getTextContent(modelNoLabelElement).replaceAll("[:：]", "").equals(modelNoSelector.get(MODEL_NO_LABEL_NAME_KEY))) {

				LOGGER.info(String.format("[scrapeModelNo] model no (%s) is found by selector: %s", modelNoSelector.get(MODEL_NO_LABEL_NAME_KEY), modelNoSelector.get(MODEL_NO_VALUE_SELECTOR_KEY)));
        String modelNo = Common.normalize(HtmlUtils.getTextContentWithoutDuplicatedSpaces(modelNoValueElement));
				this.productInfo.setModelNo(modelNo);
				break;
			}
		}
	}

	public void scrapeQuantity(String selector) {
		LOGGER.debug("[scrapeQuantity] in");
		String str = getText(selector);
		LOGGER.debug("[scrapeQuantity] Quantity >>>> " + str);
		Integer qty = extractInt(str);
		if (str != null && qty != null) {
			// productInfo.setQuantity(extractInt(str));
			productInfo.setQuantity(qty);
		}
	}

	public void scrapeQuantity(DomNode node, String selector) {
		LOGGER.debug("[scrapeQuantity] in");
		String str = getText(node, selector);
		LOGGER.debug("[scrapeQuantity] Quantity >>>> " + str);
		if (str != null) {
			productInfo.setQuantity(extractInt(str));
		}
	}
}
