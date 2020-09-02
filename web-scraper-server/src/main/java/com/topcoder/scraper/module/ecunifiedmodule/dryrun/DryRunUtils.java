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

    private String PREFIX_PATH = "html";

    private Integer maxRunCount;

    public DryRunUtils(Integer count) {
        this.maxRunCount = count;
    }

    public Integer getMaxRunCount() {
        return maxRunCount;
    }

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
        dryRunResultList.add(new ProductInfoList(productInfoList));
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

    private class ProductInfoList {
        @JsonProperty("product_infos")
        List<ProductInfo> productInfoList;

        public ProductInfoList(List<ProductInfo> productInfoList) {
            if (productInfoList == null) {
                this.productInfoList = new ArrayList<ProductInfo>();
            } else {
                this.productInfoList = productInfoList;
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
     *
     * @param html file path
     * @return relative html file path
     */
    private String convertToRelativePath(String htmlPath) {
        int delimiterIntex = htmlPath.lastIndexOf("/");
        return htmlPath.substring(delimiterIntex, htmlPath.length());
    }

    /**
     * Check List count
     *
     * @return if true over count
     */
    public boolean checkCountOver(List list) {
        if (maxRunCount == 0) {
            return false;
        }
        if (list.size() >= maxRunCount) {
            return true;
        }
        return false;
    }

}
