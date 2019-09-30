package com.topcoder.scraper.module.navpage;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import static com.topcoder.common.util.HtmlUtils.*;

public class NavigableProductDetailPage extends NavigablePage {

    // TrafficWebClient webClient;
    // HtmlPage page;
    private ProductInfo productInfo;

    public NavigableProductDetailPage(HtmlPage page, TrafficWebClient webClient, ProductInfo productInfo) {
        super(page, webClient);
        this.productInfo = productInfo;
    }

    public NavigableProductDetailPage(String url, TrafficWebClient webClient, ProductInfo productInfo) {
        super(url, webClient);
        this.productInfo = productInfo;
    }

    public ProductInfo getProductInfo() {
        return this.productInfo;
    }

    public void scrapeDistributor(String selector) {
		String str = getText(selector);
		System.out.println("\n Distributor >>>> " + str);
		if (str != null) {
			productInfo.setDistributor(str);
		}
	}

	public void scrapeDistributor(DomNode node, String selector) {
		String str = getText(node, selector);
		System.out.println("\n Distributor >>>> " + str);
		if (str != null) {
			productInfo.setDistributor(str);
		}
	}

	public void scrapeCode(String selector) {
		String code = getText(selector);
		System.out.println("\n Code >>>> " + code);
		if (code != null) {
			productInfo.setCode(code);
		}
	}

	public void scrapeCode(DomNode node, String selector) {
		String code = getText(node, selector);
		System.out.println("\n Code >>>> " + code);
		if (code != null) {
			productInfo.setCode(code);
		}
	}

	public void scrapeName(String selector) {
		String str = getText(selector);
		System.out.println("\n Name >>>> " + str);
		if (str != null) {
			productInfo.setName(str);
		}
	}

	public void scrapeName(DomNode node, String selector) {
		String str = getText(node, selector);
		System.out.println("\n Name >>>> " + str);
		if (str != null) {
			productInfo.setName(str);
		}
	}

	public void scrapePrice(String selector) {
		String str = getText(selector);
		System.out.println("\n Price >>>> " + str);
		if (str != null) {
			productInfo.setPrice(str);
		}
	}

	public void scrapePrice(DomNode node, String selector) {
		String str = getText(node, selector);
		System.out.println("\n Price >>>> " + str);
		if (str != null) {
			productInfo.setPrice(str);
		}
	}

	public void scrapeModelNo(String selector) {
		String str = getText(selector);
		str = str.replaceAll("[^0-9a-zA-Z\\-]", "").trim();
		System.out.println("Model No >>>> " + str);
		if (str != null) {
			productInfo.setModelNo(str);
		}
	}
	
	public void scrapeModelNo(DomNode node, String selector) {
		String str = getText(node, selector);
		str = str.replaceAll("[^0-9a-zA-Z\\-]", "").trim();
		System.out.println("Model No >>>> " + str);
		if (str != null) {
			productInfo.setModelNo(str);
		}
    }
    
	public void scrapeQuantity(String selector) { 
		String str = getText(selector); 
		System.out.println("\n Quantity >>>> " + str);
		Integer qty = extractInt(str);
        if(str != null && qty != null) {
			//productInfo.setQuantity(extractInt(str)); 
			productInfo.setQuantity(qty);
        }
	}
	
	public void scrapeQuantity(DomNode node, String selector) { 
		String str = getText(node, selector); 
		System.out.println("\n Quantity >>>> " + str);
        if(str != null) {
            productInfo.setQuantity(extractInt(str)); 
        }
    }
}