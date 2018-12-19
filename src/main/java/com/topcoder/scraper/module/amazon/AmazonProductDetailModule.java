package com.topcoder.scraper.module.amazon;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.scraper.config.AmazonProperty;
import com.topcoder.scraper.dao.ProductDAO;
import com.topcoder.scraper.model.ProductInfo;
import com.topcoder.scraper.module.ProductDetailModule;
import com.topcoder.scraper.service.ProductService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.topcoder.scraper.util.HtmlUtils.getTextContent;
import static com.topcoder.scraper.util.HtmlUtils.getTextContentWithoutDuplicatedSpaces;

/**
 * Amazon implementation of ProductDetailModule
 */
@Component
public class AmazonProductDetailModule extends ProductDetailModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonProductDetailModule.class);

  private final AmazonProperty property;
  private final WebClient webClient;
  private final ProductService productService;

  @Autowired
  public AmazonProductDetailModule(
    AmazonProperty property,
    WebClient webClient,
    ProductService productService) {
    this.property = property;
    this.webClient = webClient;
    this.productService = productService;
  }

  @Override
  public String getECName() {
    return "amazon";
  }


  @Override
  public void fetchProductDetailList() {
    List<ProductDAO> products = this.productService.getAllFetchInfoStatusIsNull();

    products.forEach(product -> {
      try {
        fetchProductDetail(product);
      } catch (IOException | IllegalStateException e) {
        LOGGER.error(String.format("Fail to fetch product %s, please try again.", product.getProductCode()));
      }
    });
  }

  /**
   * Fetch product information from amazon
   * and save in database
   * @param product the product dao
   * @throws IOException webclient exception
   */
  private void fetchProductDetail(ProductDAO product) throws IOException {
    String productUrl = property.getProductUrl() + product.getProductCode();
    LOGGER.info("Product url " + productUrl);

    HtmlPage productPage = webClient.getPage(productUrl);

    fetchProductInfo(productPage, product);
    fetchCategoryRanking(productPage, product);
    updateProductFetchInfoStatus(product, "updated");
  }


  private void updateProductFetchInfoStatus(ProductDAO product, String status) {
    productService.updateFetchInfoStatus(product.getId(), status);
  }

  /**
   * Update product information
   * @param productPage the product detail page
   * @param product the product dao
   */
  private void fetchProductInfo(HtmlPage productPage, ProductDAO product) {
    HtmlElement priceElement = productPage.querySelector(property.getCrawling().getProductDetailPage().getPrice());
    if (priceElement == null) {
      LOGGER.info(String.format("Could not find price info for product %s:%s",
        product.getEcSite(), product.getProductCode()));
      return;
    }

    // probably could update more fields
    String price = getTextContentWithoutDuplicatedSpaces(priceElement);

    // special case handle, for example
    // https://www.amazon.com/gp/product/B016KBVBCS
    // current value of price is $ 75 55
    String[] priceArray = price.split(" ");
    if (priceArray.length == 3) {
      price = String.format("%s%s.%s", priceArray[0], priceArray[1], priceArray[2]);
    }

    String name = getTextContent(productPage.querySelector(property.getCrawling().getProductDetailPage().getName()));

    ProductInfo info = new ProductInfo();
    info.setPrice(price);
    info.setName(name);
    productService.updateProduct(product.getId(), info);
  }
  /**
   * Find category ranking and save in database
   * @param productPage the product detail page
   * @param product the product dao
   */
  private void fetchCategoryRanking(HtmlPage productPage, ProductDAO product) {
    List<String> categoryInfoList = fetchCategoryInfoList(productPage, product);

    for (String data : categoryInfoList) {

      // categoryInfo = [rank] [in] [category path]
      // in may contain ascii char number 160, so replace it with space
      String[] categoryInfo = data.replace("\u00A0", " ").split(" ", 3);

      // remove possible leading # and comma, then convert to int
      int rank = Integer.valueOf(categoryInfo[0].replace("#", "").replace(",", ""));

      // remove See [Tt]op 100 info from category path
      String path = categoryInfo[2];
      int topIndex = path.indexOf(" (See ");
      if (topIndex != -1) {
        path = path.substring(0, topIndex);
      }

      productService.addCategoryRanking(product.getId(), path, rank);
    }
  }

  /**
   * Fetch category info list from webpage
   * There are different pages from amazon
   * from li tag or a table
   * @param page the product page
   * @return list of
   */
  private List<String> fetchCategoryInfoList(HtmlPage page, ProductDAO product) {
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
      product.getEcSite(), product.getProductCode()));
    return new ArrayList<>();
  }

}
