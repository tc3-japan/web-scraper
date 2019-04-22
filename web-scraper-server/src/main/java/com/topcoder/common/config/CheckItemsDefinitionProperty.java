package com.topcoder.common.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

/**
 * Check items definition property
 */
@Component
@PropertySources({
        @PropertySource(factory = YamlPropertySourceFactory.class, value = {"classpath:check-items-definition.yaml"}),
        @PropertySource(factory = YamlPropertySourceFactory.class, value = {"file:${checkItemsFile}"}, ignoreResourceNotFound = true)
})
@ConfigurationProperties
public class CheckItemsDefinitionProperty {

  /**
   * Represents `products` in check-items-definition.yaml
   */
  public static class CheckProduct {
    private String productName;
    private String productQuantity;
    private String unitPrice;
    private String productDistributor;
    private String categories;

    public String getProductName() {
      return productName;
    }

    public String getProductQuantity() {
      return productQuantity;
    }

    public String getUnitPrice() {
      return unitPrice;
    }

    public String getProductDistributor() {
      return productDistributor;
    }

    public String getCategories() {
      return categories;
    }

    public void setProductName(String productName) {
      this.productName = productName;
    }

    public void setProductQuantity(String productQuantity) {
      this.productQuantity = productQuantity;
    }

    public void setUnitPrice(String unitPrice) {
      this.unitPrice = unitPrice;
    }

    public void setProductDistributor(String productDistributor) {
      this.productDistributor = productDistributor;
    }

    public void setCategories(String categories) {
      this.categories = categories;
    }
  }

  /**
   * Represents `check_items` in check-items-definition.yaml
   */
  public static class CheckItems {
    private String orderNo;
    private String orderDate;
    private String totalAmount;
    private String deliveryStatus;
    private CheckProduct products;

    public String getOrderNo() {
      return orderNo;
    }

    public String getOrderDate() {
      return orderDate;
    }

    public String getTotalAmount() {
      return totalAmount;
    }

    public String getDeliveryStatus() {
      return deliveryStatus;
    }

    public CheckProduct getProducts() {
      return products;
    }

    public void setOrderNo(String orderNo) {
      this.orderNo = orderNo;
    }

    public void setOrderDate(String orderDate) {
      this.orderDate = orderDate;
    }

    public void setTotalAmount(String totalAmount) {
      this.totalAmount = totalAmount;
    }

    public void setDeliveryStatus(String deliveryStatus) {
      this.deliveryStatus = deliveryStatus;
    }

    public void setProducts(CheckProduct products) {
      this.products = products;
    }
  }

  /**
   * Represents `check_pages` in check-items-definition.yaml
   */
  public static class CheckItemsCheckPage {
    private String pageName;
    private CheckItems checkItems;

    public String getPageName() {
      return pageName;
    }

    public CheckItems getCheckItems() {
      return checkItems;
    }

    public void setPageName(String pageName) {
      this.pageName = pageName;
    }

    public void setCheckItems(CheckItems checkItems) {
      this.checkItems = checkItems;
    }
  }

  private String ecSite;
  private List<CheckItemsCheckPage> checkPages;

  public String getEcSite() {
    return ecSite;
  }

  public List<CheckItemsCheckPage> getCheckPages() {
    return checkPages;
  }

  public void setEcSite(String ecSite) {
    this.ecSite = ecSite;
  }

  public void setCheckPages(List<CheckItemsCheckPage> checkPages) {
    this.checkPages = checkPages;
  }
}
