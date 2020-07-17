package com.topcoder.scraper.module.ecunifiedmodule.dryrun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;

/**
 * Utility of dry run
 */
public class DryRunUtils {

  public final static String SEARCH_KEYWORD = "パソコン";
  private String ROOT_DIRECTORY = "logs";
  private String PREFIX_PATH = "html";

  /**
   * Called by DryRunPurchasehistoryModule
   */
  public List<Object> toJsonOfDryRunPurchasehistoryModule(List<PurchaseHistory> purchaseHistoryList, List<String> htmlPathList) {
    List<Object> dryRunResultList = new ArrayList<>();
    dryRunResultList.add(purchaseHistoryList);
    dryRunResultList.add(new HtmlPath(getRelativePathList(htmlPathList)));
    return dryRunResultList;
  }

  /**
   * Called by DryRunProductModule
   */
  public List<Object> toJsonOfDryRunProductModule(List<ProductInfo> productInfoList, List<String> htmlPathList) {
    List<Object> dryRunResultList = new ArrayList<>();
    dryRunResultList.add(productInfoList);
    dryRunResultList.add(new HtmlPath(getRelativePathList(htmlPathList)));
    return dryRunResultList;
  }

  /**
   * Called by DryRunProductSearchModule
   */
  public List<Object> toJsonOfDryRunProductSearchModule(List<String> productCodeList, List<String> htmlPathList) {
    List<Object> dryRunResultList = new ArrayList<>();
    dryRunResultList.add(new ProductCode(productCodeList));
    dryRunResultList.add(new HtmlPath(getRelativePathList(htmlPathList)));
    return dryRunResultList;
  }

  private class HtmlPath {
    @JsonProperty("urls")
    List<String> htmlPathList;
    public HtmlPath(List<String> htmlPathList) {
      if (htmlPathList == null) {
        this.htmlPathList = Arrays.asList("");
      } else {
        this.htmlPathList = htmlPathList;
      }
    }
  }

  private class ProductCode {
    @JsonProperty("product_code")
    List<String> productCodeList;
    public ProductCode(List<String> productCodeList) {
      if (productCodeList == null) {
        this.productCodeList = Arrays.asList("");
      } else {
        this.productCodeList = productCodeList;
      }
    }
  }

  private List<String> getRelativePathList(List<String> htmlPathList) {
    List<String> relativeHtmlPathList = new ArrayList<String>();
    for (int i = 0; i < htmlPathList.size(); i++) {
      String htmlPath = htmlPathList.get(i);
     relativeHtmlPathList.add(PREFIX_PATH + convertToRelativePath(htmlPath));
    }
    return relativeHtmlPathList;
  }

  /**
   * Change path from abstract to relative that start at ROOT_DIRECTORY folder
   * @param html file path
   * @return relative html file path
   */
  private String convertToRelativePath(String htmlPath) {
    int delimiterIntex = htmlPath.lastIndexOf(ROOT_DIRECTORY);
    return htmlPath.substring(delimiterIntex + ROOT_DIRECTORY.length(), htmlPath.length());
  }

}
