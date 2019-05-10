package com.topcoder.common.util;

import com.topcoder.scraper.Consts;
import com.topcoder.common.config.CheckItemsDefinitionProperty;
import com.topcoder.common.config.YamlPropertySourceFactory;
import com.topcoder.common.model.ProductCheckResultDetail;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.model.PurchaseHistoryCheckResultDetail;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
  CheckItemsDefinitionProperty.class,
  YamlPropertySourceFactory.class
})
@EnableConfigurationProperties
public class CheckUtilsTest {

  @Autowired
  private CheckItemsDefinitionProperty property;

  @Test
  public void testCheckProductInfoName() {
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = getCheckPage(Consts.PRODUCT_DETAIL_PAGE_NAME);

    ProductInfo dbProductInfo = createProductInfo();

    ProductInfo productInfo = createProductInfo();
    productInfo.setName("name2");

    ProductCheckResultDetail result = CheckUtils.checkProductInfo(
      checkItemsCheckPage, dbProductInfo, productInfo);

    assertEquals("NG(NOT-EQUAL, name, name2)", result.getName());
    assertEquals("OK", result.getPrice());
    assertEquals("OK", result.getDistributor());
    assertEquals("OK", result.getQuantity());
    assertFalse(result.isOk());
  }

  @Test
  public void testCheckProductInfoPriceFormat() {
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = getCheckPage(Consts.PRODUCT_DETAIL_PAGE_NAME);

    ProductInfo dbProductInfo = createProductInfo();

    ProductInfo productInfo = createProductInfo();
    productInfo.setPrice("abc");

    ProductCheckResultDetail result = CheckUtils.checkProductInfo(
      checkItemsCheckPage, dbProductInfo, productInfo);

    assertEquals("NG(NOT-MATCH, ^\\$[0-9.]+, abc)", result.getPrice());
    assertEquals("OK", result.getName());
    assertEquals("OK", result.getDistributor());
    assertEquals("OK", result.getQuantity());
    assertFalse(result.isOk());
  }

  @Test
  public void testCheckProductInfoQuantity() {
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = getCheckPage(Consts.PRODUCT_DETAIL_PAGE_NAME);

    ProductInfo dbProductInfo = createProductInfo();

    ProductInfo productInfo = createProductInfo();
    productInfo.setQuantity(1);

    ProductCheckResultDetail result = CheckUtils.checkProductInfo(
      checkItemsCheckPage, dbProductInfo, productInfo);

    assertEquals("NG(NOT-EQUAL, 2, 1)", result.getQuantity());
    assertEquals("OK", result.getPrice());
    assertEquals("OK", result.getDistributor());
    assertEquals("OK", result.getName());
    assertFalse(result.isOk());
  }

  @Test
  public void testCheckProductInfoDistributor() {
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = getCheckPage(Consts.PRODUCT_DETAIL_PAGE_NAME);

    ProductInfo dbProductInfo = createProductInfo();

    ProductInfo productInfo = createProductInfo();
    productInfo.setDistributor("distributor2");

    ProductCheckResultDetail result = CheckUtils.checkProductInfo(
      checkItemsCheckPage, dbProductInfo, productInfo);

    assertEquals("NG(NOT-EQUAL, distributor, distributor2)", result.getDistributor());
    assertEquals("OK", result.getPrice());
    assertEquals("OK", result.getName());
    assertEquals("OK", result.getQuantity());
    assertFalse(result.isOk());
  }

  @Test
  public void testCheckProductInfoNameNotExist() {
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = getCheckPage(Consts.PRODUCT_DETAIL_PAGE_NAME);

    ProductInfo dbProductInfo = createProductInfo();

    ProductInfo productInfo = createProductInfo();
    productInfo.setName(null);

    ProductCheckResultDetail result = CheckUtils.checkProductInfo(
      checkItemsCheckPage, dbProductInfo, productInfo);

    assertEquals("NG(NOT-EXIST, name, <BLANK>)", result.getName());
    assertEquals("OK", result.getPrice());
    assertEquals("OK", result.getDistributor());
    assertEquals("OK", result.getQuantity());
    assertFalse(result.isOk());
  }

  @Test
  public void testCheckPurchaseHistoryListDeliveryStatus() throws ParseException {
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = getCheckPage(Consts.PURCHASE_HISTORY_LIST_PAGE_NAME);

    PurchaseHistory dbPurchaseHistory = createPurchaseHistory();

    PurchaseHistory purchaseHistory = createPurchaseHistory();
    purchaseHistory.setDeliveryStatus("FAILURE");

    List<PurchaseHistoryCheckResultDetail> resultList = CheckUtils.checkPurchaseHistoryList(
      checkItemsCheckPage,
      Arrays.asList(dbPurchaseHistory),
      Arrays.asList(purchaseHistory));

    assertEquals(1, resultList.size());

    PurchaseHistoryCheckResultDetail result = resultList.get(0);

    assertNull(result.getUserId());
    assertEquals("NG(NOT-EQUAL, COMPLETE, FAILURE)", result.getDeliveryStatus());
    assertEquals("OK", result.getOrderDate());
    assertEquals("OK", result.getOrderNumber());
    assertEquals("OK", result.getTotalAmount());
    assertFalse(result.isOk());

    assertEquals(1, result.getProducts().size());
    ProductCheckResultDetail productResult = result.getProducts().get(0);
    assertTrue(productResult.isOk());
  }

  @Test
  public void testCheckPurchaseHistoryListDate() throws ParseException {
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = getCheckPage(Consts.PURCHASE_HISTORY_LIST_PAGE_NAME);

    PurchaseHistory dbPurchaseHistory = createPurchaseHistory();

    PurchaseHistory purchaseHistory = createPurchaseHistory();
    purchaseHistory.setOrderDate(DateUtils.fromString("July 1, 2018"));

    List<PurchaseHistoryCheckResultDetail> resultList = CheckUtils.checkPurchaseHistoryList(
      checkItemsCheckPage,
      Arrays.asList(dbPurchaseHistory),
      Arrays.asList(purchaseHistory));

    assertEquals(1, resultList.size());

    PurchaseHistoryCheckResultDetail result = resultList.get(0);

    assertNull(result.getUserId());
    
    assertNotNull(result.getOrderDate());
    assertTrue("result.getOrderDate().length() should be >= 2", result.getOrderDate().length() >= 2);
    assertEquals("NG", result.getOrderDate().substring(0, 2));
    
    assertEquals("OK", result.getOrderNumber());
    assertEquals("OK", result.getDeliveryStatus());
    assertEquals("OK", result.getTotalAmount());
    assertFalse(result.isOk());

    assertEquals(1, result.getProducts().size());
    ProductCheckResultDetail productResult = result.getProducts().get(0);
    assertTrue(productResult.isOk());
  }

  @Test
  public void testCheckPurchaseHistoryListAmount() throws ParseException {
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = getCheckPage(Consts.PURCHASE_HISTORY_LIST_PAGE_NAME);

    PurchaseHistory dbPurchaseHistory = createPurchaseHistory();

    PurchaseHistory purchaseHistory = createPurchaseHistory();
    purchaseHistory.setTotalAmount("$200");

    List<PurchaseHistoryCheckResultDetail> resultList = CheckUtils.checkPurchaseHistoryList(
      checkItemsCheckPage,
      Arrays.asList(dbPurchaseHistory),
      Arrays.asList(purchaseHistory));

    assertEquals(1, resultList.size());

    PurchaseHistoryCheckResultDetail result = resultList.get(0);

    assertNull(result.getUserId());
    assertEquals("NG(NOT-EQUAL, $100, $200)", result.getTotalAmount());
    assertEquals("OK", result.getOrderNumber());
    assertEquals("OK", result.getDeliveryStatus());
    assertEquals("OK", result.getOrderDate());
    assertFalse(result.isOk());

    assertEquals(1, result.getProducts().size());
    ProductCheckResultDetail productResult = result.getProducts().get(0);
    assertTrue(productResult.isOk());
  }

  @Test
  public void testCheckPurchaseHistoryListProductChange() throws ParseException {
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage = getCheckPage(Consts.PURCHASE_HISTORY_LIST_PAGE_NAME);

    PurchaseHistory dbPurchaseHistory = createPurchaseHistory();

    PurchaseHistory purchaseHistory = createPurchaseHistory();

    ProductInfo productInfo = createProductInfo();
    productInfo.setName("name2");

    purchaseHistory.setProducts(Arrays.asList(productInfo));

    List<PurchaseHistoryCheckResultDetail> resultList = CheckUtils.checkPurchaseHistoryList(
      checkItemsCheckPage,
      Arrays.asList(dbPurchaseHistory),
      Arrays.asList(purchaseHistory));

    assertEquals(1, resultList.size());

    PurchaseHistoryCheckResultDetail result = resultList.get(0);

    assertNull(result.getUserId());
    assertEquals("OK", result.getTotalAmount());
    assertEquals("OK", result.getOrderNumber());
    assertEquals("OK", result.getDeliveryStatus());
    assertEquals("OK", result.getOrderDate());

    assertFalse(result.isOk());

    assertEquals(1, result.getProducts().size());
    ProductCheckResultDetail productResult = result.getProducts().get(0);
    assertFalse(productResult.isOk());
  }


  private CheckItemsDefinitionProperty.CheckItemsCheckPage getCheckPage(String name) {
    return property.getCheckPages()
      .stream()
      .filter(checkPage -> checkPage.getPageName().equals(name))
      .findFirst().get();
  }

  private ProductInfo createProductInfo() {
    ProductInfo productInfo = new ProductInfo();
    productInfo.setCode("code");
    productInfo.setName("name");
    productInfo.setPrice("$100");
    productInfo.setDistributor("distributor");
    productInfo.setQuantity(2);
    return productInfo;
  }

  private PurchaseHistory createPurchaseHistory() throws ParseException {
    return new PurchaseHistory(
      "testUser@google.com",
      "number",
      DateUtils.fromString("June 1, 2018"),
      "$100",
      Arrays.asList(createProductInfo()),
      "COMPLETE");
  }
}
