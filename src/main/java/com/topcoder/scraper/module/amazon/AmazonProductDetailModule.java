package com.topcoder.scraper.module.amazon;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.topcoder.scraper.config.AmazonProperty;
import com.topcoder.scraper.dao.ProductDAO;
import com.topcoder.scraper.module.ProductDetailModule;
import com.topcoder.scraper.service.ProductService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.topcoder.scraper.util.HtmlUtils.getTextContent;

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

    try {
      fetchProductDetail(products.get(15));
    } catch (IOException e) {
      e.printStackTrace();
    }
    products.forEach(product -> {
      try {
        fetchProductDetail(product);
      } catch (IOException | IllegalStateException e) {
        LOGGER.error("Fail to fetch product " + product.getProductCode());
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
  }

  /**
   * Update product information
   * @param productPage the product detail page
   * @param product the product dao
   */
  private void fetchProductInfo(HtmlPage productPage, ProductDAO product) {
    HtmlElement priceElement = productPage.querySelector(property.getCrawling().getPrice());
    if (priceElement == null) {
      return;
    }

    // TODO probably could update more fields
    String price = getTextContent(priceElement);
    productService.update(product.getId(), price, "updated");
  }
  /**
   * Find category ranking and save in database
   * @param productPage the product detail page
   * @param product the product dao
   */
  private void fetchCategoryRanking(HtmlPage productPage, ProductDAO product) {
    List<String> categoryInfoList = fetchCategoryInfoList(productPage);

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
        path = path.substring(topIndex);
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
  private List<String> fetchCategoryInfoList(HtmlPage page) {
    DomNode node = page.querySelector(property.getCrawling().getSalesRank());

    // category ranking is from li#salesrank
    if (node != null) {
      List<DomNode> ranks = node.querySelectorAll("ul > li > span:nth-of-type(1)");
      List<DomNode> paths = node.querySelectorAll("ul > li > span:nth-of-type(2)");

      List<String> categoryInfoList = new ArrayList<>();
      for (int i = 0; i < ranks.size(); i++) {
        categoryInfoList.add(
          getTextContent((HtmlElement) ranks.get(i)) + " " + getTextContent((HtmlElement) paths.get(i)));
      }

      return categoryInfoList;
    }

    node = page.querySelector(property.getCrawling().getProductInfoTable());

    // category ranking is from product table
    if (node != null) {
      List<DomNode> trList = node.querySelectorAll("tbody > tr");
      for (DomNode tr : trList) {
        if (getTextContent(tr.querySelector("th")).contains("Rank")) {
          List<DomNode> spanList = tr.querySelectorAll("td > span > span");
          return spanList.stream().map(span -> getTextContent((HtmlElement) span)).collect(Collectors.toList());
        }

      }
      return new ArrayList<>();
    }

    return new ArrayList<>();
  }

}