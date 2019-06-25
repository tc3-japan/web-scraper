package com.topcoder.scraper.module.yahoo.crawler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.exception.SessionExpiredException;
import com.topcoder.scraper.service.WebpageService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import static com.topcoder.common.util.HtmlUtils.*;

/**
 * Crawl yahoo product detail page
 */
public class YahooProductDetailCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(YahooProductDetailCrawler.class);
  private String siteName;
  private final WebpageService webpageService;

  public YahooProductDetailCrawler(
    String siteName,
    WebpageService webpageService) {
    this.siteName = siteName;
    this.webpageService = webpageService;
  }

  /**
   *
   * Fetch product information
   * @param webClient the web client
   * @param productCode the product code
   * @param saveHtml true if product html page will be saved
   * @return YahooProductDetailCrawlerResult
   * @throws IOException
   */
  public YahooProductDetailCrawlerResult fetchProductInfo(TrafficWebClient webClient, String productCode, boolean saveHtml) throws IOException {
    
    System.out.println("Getting product info by Product Code!");

    String productName = productCode;
    YahooProductDetailCrawlerResult result = getProductInfoByCode(webClient,productName,saveHtml);

    return result; 
  }


  private YahooProductDetailCrawlerResult getProductInfoByCode(TrafficWebClient webClient, String searchWords, boolean saveHtml) throws IOException {

    String htmlPath = "https://store.shopping.yahoo.co.jp/"+searchWords;
    System.out.println("\n>>>> searching for "+htmlPath);
    HtmlPage page = webClient.getPage(htmlPath);

    webpageService.save("yahoo-search-result", siteName, page.getWebResponse().getContentAsString());

    String shouhinCode = page.querySelector("#abuserpt > p:nth-child(3)").asText(); //???
    System.out.println("\n>>>> shouhinCode:"+shouhinCode);
    String prodName = page.querySelector("div.elTitle > h2:nth-child(1)").asText();
    System.out.println("\n>>>> productName:"+prodName);
    String vendorName = page.querySelector("dt.elStore > a:nth-child(1)").asText();
    System.out.println("\n>>>> vendorName:"+vendorName);
    String prodPrice = page.querySelector(".elNum").asText();
    System.out.println("\n>>>> price:"+prodPrice);
    String modelNo = null;
  
    ProductInfo productInfo = new ProductInfo();
    productInfo.setCode(shouhinCode);
    productInfo.setDistributor(vendorName);
    productInfo.setName(prodName);
    productInfo.setPrice(prodPrice.replaceAll(",", "")); // TODO ??
    productInfo.setModelNo(modelNo);

    productInfo = null;
    return new YahooProductDetailCrawlerResult(productInfo, htmlPath);
  }
  

  /**
   * Find category ranking and save in database
   * @param productPage the product detail page
   * @param info the product info to be updated
   * @param productCode the product code
   */
  private void fetchCategoryRanking(HtmlPage productPage, ProductInfo info, String productCode) {
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

  /**
   * Fetch category info list from webpage
   * There are different pages from amazon... /yahoo??
   * from li tag or a table
   * @param page the product page
   * @param productCode the product code
   * @return list of category string
   */
  private List<String> fetchCategoryInfoList(HtmlPage page, String productCode) {
    System.out.println("Pretending to fetch info list!");
    /*
    DomNode node = page.querySelector(property.getCrawling().getProductDetailPage().getSalesRank());

    // category ranking is from li#salesrank
    if (node != null) {
      List<String> categoryInfoList = new ArrayList<>();

      // get first rank and category path
      Pattern pattern = Pattern.compile("(#.*? in .*?)\\(");
      Matcher matcher = pattern.matcher(node.getTextContent());
      if (matcher.find()) {
        String firstRankAndPath = matcher.group(1).trim();
        categoryInfoList.add(firstRankAndPath);
      }

      // get rest of ranks and category paths
      List<DomNode> ranks = node.querySelectorAll("ul > li > span:nth-of-type(1)");
      List<DomNode> paths = node.querySelectorAll("ul > li > span:nth-of-type(2)");
      for (int i = 0; i < ranks.size(); i++) {
        categoryInfoList.add(
          getTextContent((HtmlElement) ranks.get(i)) + " " + getTextContent((HtmlElement) paths.get(i)));
      }

      return categoryInfoList;
    }

    node = page.querySelector(property.getCrawling().getProductDetailPage().getProductInfoTable());

    // category ranking is from product table
    if (node != null) {
      List<DomNode> trList = node.querySelectorAll("tbody > tr");
      for (DomNode tr : trList) {
        if (getTextContent(tr.querySelector("th")).contains("Rank")) {
          List<DomNode> spanList = tr.querySelectorAll("td > span > span");
          return spanList.stream().map(span -> getTextContent((HtmlElement) span)).collect(Collectors.toList());
        }
      }
    }

    LOGGER.info(String.format("Could not find category rankings for product %s:%s",
      this.siteName, productCode));
      */
    return new ArrayList<>();
  }
  
  /**
  *
  * Search and Fetch product information
  * @param webClient the web client
  * @param modelNo the mode no
  * @param saveHtml true if product html page will be saved
  * @return YahooProductDetailCrawlerResult
  * @throws IOException
  */
  public YahooProductDetailCrawlerResult searchProductAndFetchProductInfoByModelNo(TrafficWebClient webClient, String modelNo, boolean saveHtml) throws IOException  {
	  
	  String productCode = searchProduct(webClient,modelNo);
	  
	  if (productCode == null) {
	      LOGGER.info(String.format("Could not find name info for model no %s",modelNo));
	      return null;
	    }
	  
	  return fetchProductInfo(webClient, productCode, saveHtml);
  }
  
  /**
  *
  * Search product
  * @param webClient the web client
  * @param  search word
  * @return String asin no(product code)
  * @throws IOException
  */
  private String searchProduct(TrafficWebClient webClient, String searchWord) throws IOException {
    System.out.println("Pretending to fetch searchProduct!");
	  /*
	  String productCode = null;
	  String searchUrl = property.getSearchUrl() + searchWord;
	  LOGGER.info("Product url " + searchUrl);
	  
	  HtmlPage productPage = webClient.getPage(searchUrl);
	  
	  //10 times try
	  for(int index = 0; index < 10 ; index++) {
		  String searchResultSelector = property.getCrawling().getSearchProductPage().getProductSelector() + index + ")";
		  HtmlElement element = productPage.querySelector(searchResultSelector);
		  
		  if (element == null) {
			  continue;
		  } 
		  
		  //skip ad product
		  if(element.getAttribute("class").contains(property.getCrawling().getSearchProductPage().getAdProductClass())) {
			  LOGGER.info(String.format("Skip ad product with search word = %s",searchWord));
			  continue;
		  }
		  
		  //get asin no
		  productCode = element.getAttribute(property.getCrawling().getSearchProductPage().getProductCodeAttribute());

		  if(productCode == null) {
			  continue;
		  }
		  
		  LOGGER.info(String.format("Product is found with search word = %s, product code is %s", searchWord, productCode));
		  return productCode;
	  }
	  
    LOGGER.info(String.format("Could not find product with search word = %s",searchWord));
    */
	  return null;
  }
}
