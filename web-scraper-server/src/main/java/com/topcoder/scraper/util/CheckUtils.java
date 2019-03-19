package com.topcoder.scraper.util;

import com.topcoder.scraper.Consts;
import com.topcoder.scraper.config.CheckItemsDefinitionProperty;
import com.topcoder.scraper.model.ProductCheckResultDetail;
import com.topcoder.scraper.model.ProductInfo;
import com.topcoder.scraper.model.PurchaseHistory;
import com.topcoder.scraper.model.PurchaseHistoryCheckResultDetail;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CheckUtils {
  public static String equivalenceCheck(String oldValue, String newValue) {
    if (newValue == null) {
      return String.format("%s(%s, %s, %s)",
        Consts.CHECK_RESULT_NG,
        Consts.CHECK_RESULT_NOT_EXIST,
        oldValue,
        Consts.CHECK_RESULT_BLANK);
    }
    if (!oldValue.equals(newValue)) {
      return String.format("%s(%s, %s, %s)",
        Consts.CHECK_RESULT_NG,
        Consts.CHECK_RESULT_NOT_EQUAL,
        oldValue,
        newValue);
    }

    return Consts.CHECK_RESULT_OK;
  }

  public static String formatCheck(String definition, String oldValue, String newValue) {
    String regex = definition.split(":")[1];
    if (newValue == null) {
      return String.format("%s(%s, %s, %s)",
        Consts.CHECK_RESULT_NG,
        Consts.CHECK_RESULT_NOT_EXIST,
        oldValue,
        Consts.CHECK_RESULT_BLANK);
    }
    if (!newValue.matches(regex)) {
      return String.format("%s(%s, %s, %s)",
        Consts.CHECK_RESULT_NG,
        Consts.CHECK_RESULT_NOT_MATCH,
        regex,
        newValue);
    }

    return Consts.CHECK_RESULT_OK;
  }

  public static String check(String definition, String oldValue, String newValue) {
    if (definition == null) {
      return null;
    }

    if (definition.equals(Consts.CHECK_EQUIVALENCE)) {
      return equivalenceCheck(oldValue, newValue);
    } else if (definition.startsWith(Consts.CHECK_FORMAT)) {
      return formatCheck(definition, oldValue, newValue);
    } else {
      throw new RuntimeException("Unknown check method " + definition);
    }
  }

  public static List<PurchaseHistoryCheckResultDetail> checkPurchaseHistoryList(
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage,
    List<PurchaseHistory> dbPurchaseHistoryList,
    List<PurchaseHistory> purchaseHistoryList
  ) {
    List<PurchaseHistoryCheckResultDetail> results = dbPurchaseHistoryList
      .stream()
      .map(dbPurchaseHistory -> checkPurchaseHistory(checkItemsCheckPage, dbPurchaseHistory, purchaseHistoryList))
      .filter(Objects::nonNull)
      .collect(Collectors.toList());

    return results;
  }

  public static PurchaseHistoryCheckResultDetail checkPurchaseHistory(CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage, PurchaseHistory dbPurchaseHistory, List<PurchaseHistory> purchaseHistoryList) {
    Optional<PurchaseHistory> matchedPurchaseHistory = purchaseHistoryList.stream().filter(ph -> ph.getOrderNumber().equals(dbPurchaseHistory.getOrderNumber())).findFirst();

    if (!matchedPurchaseHistory.isPresent()) {
      // new Order
      return null;

    } else {
      PurchaseHistoryCheckResultDetail result = new PurchaseHistoryCheckResultDetail();

      String orderNoCheckResult = CheckUtils.check(checkItemsCheckPage.getCheckItems().getOrderNo(), dbPurchaseHistory.getOrderNumber(), matchedPurchaseHistory.get().getOrderNumber());
      if (orderNoCheckResult != null) {
        result.setOrderNumber(orderNoCheckResult);
        if (!orderNoCheckResult.equals(Consts.CHECK_RESULT_OK)) {
          result.setOk(false);
        }
      }

      String orderDateCheckResult = CheckUtils.check(checkItemsCheckPage.getCheckItems().getOrderDate(), String.valueOf(dbPurchaseHistory.getOrderDate()), String.valueOf(matchedPurchaseHistory.get().getOrderDate()));
      if (orderDateCheckResult != null) {
        result.setOrderDate(orderDateCheckResult);
        if (!orderDateCheckResult.equals(Consts.CHECK_RESULT_OK)) {
          result.setOk(false);
        }
      }

      String orderAmountCheckResult = CheckUtils.check(checkItemsCheckPage.getCheckItems().getTotalAmount(), dbPurchaseHistory.getTotalAmount(), matchedPurchaseHistory.get().getTotalAmount());
      if (orderAmountCheckResult != null) {
        result.setTotalAmount(orderAmountCheckResult);
        if (!orderAmountCheckResult.equals(Consts.CHECK_RESULT_OK)) {
          result.setOk(false);
        }
      }

      String orderDeliveryStatusCheckResult = CheckUtils.check(checkItemsCheckPage.getCheckItems().getDeliveryStatus(), dbPurchaseHistory.getDeliveryStatus(), matchedPurchaseHistory.get().getDeliveryStatus());
      if (orderDeliveryStatusCheckResult != null) {
        result.setDeliveryStatus(orderDeliveryStatusCheckResult);
        if (!orderDeliveryStatusCheckResult.equals(Consts.CHECK_RESULT_OK)) {
          result.setOk(false);
        }
      }

      List<ProductInfo> dbProductInfoList = dbPurchaseHistory.getProducts();

      List<ProductCheckResultDetail> productResults =
        dbProductInfoList.stream().map(dbProductInfo -> {
          ProductInfo matchedProductInfo = matchedPurchaseHistory.get().getProducts().stream().filter(pi -> pi.getCode().equals(dbProductInfo.getCode())).findFirst().orElse(null);

          if (matchedProductInfo == null) {
            // new ProductInfo
            return null;
          } else {

            return checkProductInfo(checkItemsCheckPage, dbProductInfo, matchedProductInfo);
          }

        }).filter(Objects::nonNull)
          .collect(Collectors.toList());

      result.setProducts(productResults);

      // if any product check result fails
      // mark purchase history as failure
      if (productResults.stream().anyMatch(r -> !r.isOk())) {
        result.setOk(false);
      }
      return result;
    }
  }


  public static ProductCheckResultDetail checkProductInfo(
    CheckItemsDefinitionProperty.CheckItemsCheckPage checkItemsCheckPage,
    ProductInfo dbProductInfo, ProductInfo productInfo) {
    ProductCheckResultDetail result = new ProductCheckResultDetail();

    String productNameCheckResult = CheckUtils.check(checkItemsCheckPage.getCheckItems().getProducts().getProductName(), dbProductInfo.getName(), productInfo.getName());
    if (productNameCheckResult != null) {
      result.setName(productNameCheckResult);
      if (!productNameCheckResult.equals(Consts.CHECK_RESULT_OK)) {
        result.setOk(false);
      }
    }
    String productQuantityCheckResult = CheckUtils.check(checkItemsCheckPage.getCheckItems().getProducts().getProductQuantity(), String.valueOf(dbProductInfo.getQuantity()), String.valueOf(productInfo.getQuantity()));
    if ((productInfo.getQuantity() != 0 || dbProductInfo.getQuantity() != 0) && productQuantityCheckResult != null) {
      result.setQuantity(productQuantityCheckResult);
      if (!productQuantityCheckResult.equals(Consts.CHECK_RESULT_OK)) {
        result.setOk(false);
      }
    }
    String productPriceCheckResult = CheckUtils.check(checkItemsCheckPage.getCheckItems().getProducts().getUnitPrice(), dbProductInfo.getPrice(), productInfo.getPrice());
    if (productPriceCheckResult != null) {
      result.setPrice(productPriceCheckResult);
      if (!productPriceCheckResult.equals(Consts.CHECK_RESULT_OK)) {
        result.setOk(false);
      }
    }
    String productDistributorCheckResult = CheckUtils.check(checkItemsCheckPage.getCheckItems().getProducts().getProductDistributor(), dbProductInfo.getDistributor(), productInfo.getDistributor());
    if (productDistributorCheckResult != null) {
      result.setDistributor(productDistributorCheckResult);
      if (!productDistributorCheckResult.equals(Consts.CHECK_RESULT_OK)) {
        result.setOk(false);
      }
    }

    if (checkItemsCheckPage.getCheckItems().getProducts().getCategories() != null) {
      for (String category : productInfo.getCategoryList()) {
        String categoryCheckResult = CheckUtils.check(checkItemsCheckPage.getCheckItems().getProducts().getCategories(), "", category);
        if (categoryCheckResult != null) {
          result.addCategory(categoryCheckResult);
          if (!categoryCheckResult.equals(Consts.CHECK_RESULT_OK)) {
            result.setOk(false);
          }
        }
      }
    }

    return result;
  }
}
