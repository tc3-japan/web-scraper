package com.topcoder.scraper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * amazon related property
 */
@Configuration
@ConfigurationProperties(prefix = "amazon")
public class AmazonProperty {

  private String username;
  private String password;
  private String url;
  private String historyUrl;
  private String productUrl;

  public static class CrawlingProperty {
    private String loginButton;
    private String emailInput;
    private String continueInput;
    private String passwordInput;
    private String submitButton;

    private String orderDate;
    private String totalAmount;
    private String orderNumber;
    private String deliveryStatus;

    private String ordersBox;
    private String productsBox;

    private String productAnchor;
    private String productDistributor;
    private String unitPrice;
    private String productQuantity;

    private String nextPage;

    private String productInfoTable;
    private String salesRank;
    private String price;

    public String getLoginButton() {
      return loginButton;
    }

    public String getEmailInput() {
      return emailInput;
    }

    public String getContinueInput() {
      return continueInput;
    }

    public String getPasswordInput() {
      return passwordInput;
    }

    public String getSubmitButton() {
      return submitButton;
    }

    public String getOrderDate() {
      return orderDate;
    }

    public String getTotalAmount() {
      return totalAmount;
    }

    public String getOrderNumber() {
      return orderNumber;
    }

    public String getDeliveryStatus() {
      return deliveryStatus;
    }

    public String getOrdersBox() {
      return ordersBox;
    }

    public String getProductsBox() {
      return productsBox;
    }

    public String getProductAnchor() {
      return productAnchor;
    }

    public String getProductDistributor() {
      return productDistributor;
    }

    public String getUnitPrice() {
      return unitPrice;
    }

    public String getProductQuantity() {
      return productQuantity;
    }

    public String getNextPage() {
      return nextPage;
    }

    public String getProductInfoTable() {
      return productInfoTable;
    }

    public String getSalesRank() {
      return salesRank;
    }

    public String getPrice() {
      return price;
    }

    public void setLoginButton(String loginButton) {
      this.loginButton = loginButton;
    }

    public void setEmailInput(String emailInput) {
      this.emailInput = emailInput;
    }

    public void setContinueInput(String continueInput) {
      this.continueInput = continueInput;
    }

    public void setPasswordInput(String passwordInput) {
      this.passwordInput = passwordInput;
    }

    public void setSubmitButton(String submitButton) {
      this.submitButton = submitButton;
    }

    public void setOrderDate(String orderDate) {
      this.orderDate = orderDate;
    }

    public void setTotalAmount(String totalAmount) {
      this.totalAmount = totalAmount;
    }

    public void setOrderNumber(String orderNumber) {
      this.orderNumber = orderNumber;
    }

    public void setDeliveryStatus(String deliveryStatus) {
      this.deliveryStatus = deliveryStatus;
    }

    public void setOrdersBox(String ordersBox) {
      this.ordersBox = ordersBox;
    }

    public void setProductsBox(String productsBox) {
      this.productsBox = productsBox;
    }

    public void setProductAnchor(String productAnchor) {
      this.productAnchor = productAnchor;
    }

    public void setProductDistributor(String productDistributor) {
      this.productDistributor = productDistributor;
    }

    public void setUnitPrice(String unitPrice) {
      this.unitPrice = unitPrice;
    }

    public void setProductQuantity(String productQuantity) {
      this.productQuantity = productQuantity;
    }

    public void setNextPage(String nextPage) {
      this.nextPage = nextPage;
    }

    public void setProductInfoTable(String productInfoTable) {
      this.productInfoTable = productInfoTable;
    }

    public void setPrice(String price) {
      this.price = price;
    }

    public void setSalesRank(String salesRank) {
      this.salesRank = salesRank;
    }
  }

  private CrawlingProperty crawling;

  public CrawlingProperty getCrawling() {
    return crawling;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getUrl() {
    return url;
  }

  public String getProductUrl() {
    return productUrl;
  }

  public String getHistoryUrl() {
    return historyUrl;
  }

  public void setCrawling(CrawlingProperty crawling) {
    this.crawling = crawling;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setHistoryUrl(String historyUrl) {
    this.historyUrl = historyUrl;
  }

  public void setProductUrl(String productUrl) {
    this.productUrl = productUrl;
  }
}
