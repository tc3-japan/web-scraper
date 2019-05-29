package com.topcoder.scraper.module.kojima.crawler;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.service.WebpageService;


public class KojimaProductDetailCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(KojimaProductDetailCrawler.class);
  private String siteName;
  private final WebpageService webpageService;

  public KojimaProductDetailCrawler(String siteName, WebpageService webpageService) {
    this.siteName = siteName;
    this.webpageService = webpageService;
  }
  
  public KojimaProductDetailCrawlerResult fetchProductInfo(TrafficWebClient webClient, String productName, boolean saveHtml) throws IOException {

    LOGGER.info("Product name " + productName);
    
    KojimaProductDetailCrawlerResult result = searchProductInfoByAnyWords(webClient,productName,saveHtml);
    
    LOGGER.info("Product name from Purchase hitosry: [" + productName + "]");
    LOGGER.info("Product name from Product page    : [" + result.getProductInfo().getName()+ "] matched: " + (productName.equals(result.getProductInfo().getName())));

    return result;
  }
  
  public KojimaProductDetailCrawlerResult fetchProductInfoWithModelNo(TrafficWebClient webClient, String modelNo, boolean saveHtml) throws IOException {
	LOGGER.info("Model no" + modelNo);
	
    KojimaProductDetailCrawlerResult result = searchProductInfoByAnyWords(webClient,modelNo,saveHtml);

    if(Objects.isNull(result) || result.getProductInfo().getModelNo() == null) {
    	LOGGER.info("Can not find Product with Model No : [" + modelNo + "]");
    	return null;
    }
    
    LOGGER.info("Product name from Registered Model No : [" + modelNo + "]");
    LOGGER.info("Product name from Product page    : [" + result.getProductInfo().getModelNo()+ "] matched: " + (modelNo.equals(result.getProductInfo().getModelNo())));

    return modelNo.equals(result.getProductInfo().getModelNo()) ? result : null;
  }
  
  private KojimaProductDetailCrawlerResult searchProductInfoByAnyWords(TrafficWebClient webClient, String searchWords, boolean saveHtml) throws IOException {

	HtmlPage topPage = webClient.getPage("https://www.kojima.net/ec/top/CSfTop.jsp");
	HtmlForm searchForm = topPage.getFormByName("search_form");
	  
	HtmlTextInput searchInput = searchForm.getInputByName("q");
	searchInput.type(searchWords);
	HtmlImageInput searchButtonInput = topPage.querySelector("#btnSearch");
	  
	HtmlPage searchResultPage = webClient.click(searchButtonInput);
	  
	DomNode firstItemAnchorNode = searchResultPage.querySelector("#category_item_list > li:first-child > a");
	if (saveHtml) {
	  webpageService.save("kojima-search-result", siteName, searchResultPage.getWebResponse().getContentAsString());
	}    
	if (firstItemAnchorNode == null) {
	  return null;
	}
	NamedNodeMap attributeMap = firstItemAnchorNode.getAttributes();
	String linkToItem = attributeMap.getNamedItem("href") != null ? attributeMap.getNamedItem("href").getNodeValue() : null;
	  
	HtmlPage productDetailPage = webClient.getPage(linkToItem);

	HtmlForm goodsForm = productDetailPage.getFormByName("Goods");
	HtmlHiddenInput stkNoHidden = goodsForm.getInputByName("GOODS_STK_NO");
	String stkNo = stkNoHidden != null ? stkNoHidden.getValueAttribute() : null;
	  
	DomNode prodNameNode = productDetailPage.querySelector("h1.htxt02");
	String prodName = prodNameNode != null ? prodNameNode.asText().replaceAll("\\n", " ").trim() : null;
	  
	DomNode vendorNameNode = prodNameNode.querySelector("span");
	String vendorName = vendorNameNode != null ? vendorNameNode.asText().trim() : null;

	DomNode prodPriceNode = productDetailPage.querySelector("td.price > span");
	String prodPrice = prodPriceNode != null ? prodPriceNode.asText().trim() : null;
	  
	DomNode modelNoNode = productDetailPage.querySelector("#item_detail > div > div.item_detail_box > table > tbody > tr:nth-child(6) > td");
	String modelNo= modelNoNode != null ? modelNoNode.asText().replaceAll("[^0-9a-zA-Z\\-]", "").trim() : null;
	  
	ProductInfo productInfo = new ProductInfo();
	productInfo.setCode(stkNo); // GOODS_STK_NO (GOODS_NO?)
	productInfo.setDistributor(vendorName);
	productInfo.setName(prodName);
	productInfo.setPrice(prodPrice.replaceAll(",", "")); // TODO
	productInfo.setModelNo(modelNo);
	//productInfo.setQuantity(1);
	  
	String savedPath = null;
	if (saveHtml) {
	  savedPath = webpageService.save("kojima-product-details", siteName, productDetailPage.getWebResponse().getContentAsString());
	}
	return new KojimaProductDetailCrawlerResult(productInfo, savedPath);
  }

}
