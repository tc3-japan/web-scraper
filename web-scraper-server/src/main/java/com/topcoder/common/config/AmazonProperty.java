package com.topcoder.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
  private String searchUrl;

  public static class LoginPage {

    private String emailInput;
    private String continueInput;
    private String passwordInput;
    private String submitButton;

    private String captchaInput1st;
    private String captchaInput2nd;
    private String captchaImage1st;
    private String captchaImage2nd;
    private String captchaSubmit1st;

    private String mfaInput;
    private String verificationCodeSubmit;
    private String verificationCodeInput;

    private String mfaLoginButton;

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

    public String getCaptchaInput1st() {
      return captchaInput1st;
    }

    public String getCaptchaInput2nd() {
      return captchaInput2nd;
    }

    public String getCaptchaImage1st() {
      return captchaImage1st;
    }

    public String getCaptchaImage2nd() {
      return captchaImage2nd;
    }

    public String getCaptchaSubmit1st() {
      return captchaSubmit1st;
    }

    public String getMfaInput() {
      return mfaInput;
    }

    public String getMfaLoginButton() {
      return mfaLoginButton;
    }

    public String getVerificationCodeSubmit() {
      return verificationCodeSubmit;
    }

    public void setVerificationCodeSubmit(String verificationCodeSubmit) {
      this.verificationCodeSubmit = verificationCodeSubmit;
    }

    public String getVerificationCodeInput() {
      return verificationCodeInput;
    }

    public void setVerificationCodeInput(String verificationCodeInput) {
      this.verificationCodeInput = verificationCodeInput;
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

    public void setCaptchaInput1st(String captchaInput1st) {
      this.captchaInput1st = captchaInput1st;
    }

    public void setCaptchaInput2nd(String captchaInput2nd) {
      this.captchaInput2nd = captchaInput2nd;
    }

    public void setCaptchaImage1st(String captchaImage1st) {
      this.captchaImage1st = captchaImage1st;
    }

    public void setCaptchaImage2nd(String captchaImage2nd) {
      this.captchaImage2nd = captchaImage2nd;
    }

    public void setCaptchaSubmit1st(String captchaSubmit1st) {
      this.captchaSubmit1st = captchaSubmit1st;
    }

    public void setMfaInput(String mfaInput) {
      this.mfaInput = mfaInput;
    }

    public void setMfaLoginButton(String mfaLoginButton) {
      this.mfaLoginButton = mfaLoginButton;
    }
  }

  public static class HomePage {

    private String ordersButton;

    public String getOrdersButton() {
      return ordersButton;
    }

    public void setOrdersButton(String ordersButton) {
      this.ordersButton = ordersButton;
    }
  }

  public static class PurchaseHistoryListPage {

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

    private String orderFilter;
    private String nextPage;

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

    public String getOrderFilter() {
      return orderFilter;
    }

    public String getNextPage() {
      return nextPage;
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

    public void setOrderFilter(String orderFilter) {
      this.orderFilter = orderFilter;
    }

    public void setNextPage(String nextPage) {
      this.nextPage = nextPage;
    }
  }
  
  public static class SearchProductPage {
	  private String productSelector;
	  private String adProductClass;
	  private String productCodeAttribute;
	  
	  public String getProductSelector() {
		  return this.productSelector;
	  }
	  
	  public String getAdProductClass() {
		  return this.adProductClass;
	  }
	  
	  public String getProductCodeAttribute() {
		  return this.productCodeAttribute;
	  }
	  
	  public void setProductSelector(String productSelector) {
		  this.productSelector = productSelector;
	  }
	  
	  public void setAdProductClass(String adProductClass) {
		  this.adProductClass = adProductClass;
	  }
	  
	  public void setproductCodeAttribute(String productCodeAttribute) {
		  this.productCodeAttribute = productCodeAttribute;
	  }
  }

  public static class ProductDetailPage {
    private String productInfoTable;
    private String salesRank;
    private List<String> prices;
    private String name;
    private List<String> modelNoLabels;
    private List<String> modelNoLabelValues;
    private List<String> modelNoValues;

    public String getProductInfoTable() {
      return productInfoTable;
    }

    public String getSalesRank() {
      return salesRank;
    }

    public List<String> getPrices() {
      return prices;
    }

    public String getName() {
      return name;
    }
    
    public List<String> getModelNoLabels() {
    	return modelNoLabels;
    }

    public List<String> getModelNoValues() {
    	return modelNoValues;
    }
    
    public List<String> getModelNoLabelValues(){
    	return modelNoLabelValues;
    }

    public void setProductInfoTable(String productInfoTable) {
      this.productInfoTable = productInfoTable;
    }

    public void setSalesRank(String salesRank) {
      this.salesRank = salesRank;
    }

    public void setPrices(List<String> prices) {
      this.prices = prices;
    }

    public void setName(String name) {
      this.name = name;
    }
    
    public void setModelNoLabels(List<String> modelNoLabels) {
    	this.modelNoLabels = modelNoLabels;
    }

    public void setModelNoValues(List<String> modelNoValues) {
    	this.modelNoValues = modelNoValues;
    }
    
    public void setModelNoLabelValues(List<String> modelNoLabelValues) {
    	this.modelNoLabelValues = modelNoLabelValues;
    }
  }

  public static class CrawlingProperty {

    private String loginButton;

    private LoginPage loginPage;
    private HomePage homePage;
    private PurchaseHistoryListPage purchaseHistoryListPage;
    private ProductDetailPage productDetailPage;
    private SearchProductPage searchProductPage;

    public String getLoginButton() {
      return loginButton;
    }

    public LoginPage getLoginPage() {
      return loginPage;
    }

    public HomePage getHomePage() {
      return homePage;
    }

    public PurchaseHistoryListPage getPurchaseHistoryListPage() {
      return purchaseHistoryListPage;
    }

    public ProductDetailPage getProductDetailPage() {
      return productDetailPage;
    }
    
    public SearchProductPage getSearchProductPage() {
      return searchProductPage;
    }


    public void setLoginButton(String loginButton) {
      this.loginButton = loginButton;
    }

    public void setLoginPage(LoginPage loginPage) {
      this.loginPage = loginPage;
    }

    public void setHomePage(HomePage homePage) {
      this.homePage = homePage;
    }

    public void setPurchaseHistoryListPage(PurchaseHistoryListPage purchaseHistoryListPage) {
      this.purchaseHistoryListPage = purchaseHistoryListPage;
    }

    public void setProductDetailPage(ProductDetailPage productDetailPage) {
      this.productDetailPage = productDetailPage;
    }
    
    public void setSearchProductPage(SearchProductPage searchProductPage) {
    	this.searchProductPage = searchProductPage;
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
  
  public String getSearchUrl() {
	return this.searchUrl;
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
  
  public void setSearchUrl(String searchUrl) {
	this.searchUrl= searchUrl;
  }
}
