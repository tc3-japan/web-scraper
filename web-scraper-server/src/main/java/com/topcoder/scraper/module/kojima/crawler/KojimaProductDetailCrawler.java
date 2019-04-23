package com.topcoder.scraper.module.kojima.crawler;

import java.io.IOException;

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
import com.topcoder.scraper.module.amazon.crawler.AmazonProductDetailCrawlerResult;
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

    HtmlPage topPage = webClient.getPage("https://www.kojima.net/ec/top/CSfTop.jsp");
    HtmlForm searchForm = topPage.getFormByName("search_form");
    
    HtmlTextInput searchInput = searchForm.getInputByName("q");
    searchInput.type(productName);
    HtmlImageInput searchButtonInput = topPage.querySelector("#btnSearch");
    
    HtmlPage searchResultPage = webClient.click(searchButtonInput);
    
    DomNode firstItemAnchorNode = searchResultPage.querySelector("#category_item_list > li:first-child > a");
    NamedNodeMap attributeMap = firstItemAnchorNode.getAttributes();
    String linkToItem = attributeMap.getNamedItem("href") != null ? attributeMap.getNamedItem("href").getNodeValue() : null;
    
    HtmlPage productDetailPage = webClient.getPage(linkToItem);

    HtmlForm goodsForm = productDetailPage.getFormByName("Goods");
    HtmlHiddenInput stkNoHidden = goodsForm.getInputByName("GOODS_STK_NO");
    String stkNo = stkNoHidden != null ? stkNoHidden.getValueAttribute() : null;
    
    DomNode prodNameNode = productDetailPage.querySelector("h1.htxt02");
    String prodName = prodNameNode != null ? prodNameNode.asText().replaceAll("\\n", " ") : null;
    
    DomNode vendorNameNode = prodNameNode.querySelector("span");
    String vendorName = vendorNameNode != null ? vendorNameNode.asText() : null;
    
    DomNode prodPriceNode = productDetailPage.querySelector("td.price > span");
    String prodPrice = prodPriceNode != null ? prodPriceNode.asText() : null;
    
    LOGGER.info("Product name from Purchase hitosry: [" + productName + "]");
    LOGGER.info("Product name from Product page    : [" + prodName    + "] matched: " + (productName.equals(prodName)));
    
    ProductInfo productInfo = new ProductInfo();
    productInfo.setCode(stkNo); // GOODS_STK_NO (GOODS_NO?)
    productInfo.setDistributor(vendorName);
    productInfo.setName(prodName);
    productInfo.setPrice(prodPrice.replaceAll(",", "")); // TODO
    //productInfo.setQuantity(1);
    
    String savedPath = null;
    if (saveHtml) {
      savedPath = webpageService.save("kojima-product-details", siteName, productDetailPage.getWebResponse().getContentAsString());
    }

    return new KojimaProductDetailCrawlerResult(productInfo, savedPath);
  }
}
