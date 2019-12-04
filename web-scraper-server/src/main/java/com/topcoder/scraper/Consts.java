package com.topcoder.scraper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Consts {

  public static final String EC_SITE_AMAZON = "amazon";
  public static final String EC_SITE_KOJIMA = "kojima";
  public static final String EC_SITE_YAHOO  = "yahoo";

  public static final List<String> ALL_SITES = new ArrayList();
  static {
    ALL_SITES.add(EC_SITE_AMAZON);
    ALL_SITES.add(EC_SITE_KOJIMA);
    ALL_SITES.add(EC_SITE_YAHOO);
  }

  public static final String SCRAPING_SCRIPT_PATH = "SCRAPING_SCRIPT_PATH";

  public static final String PURCHASE_HISTORY_LIST_PAGE_NAME = "purchase_history_list";
  public static final String PRODUCT_DETAIL_PAGE_NAME        = "product_detail";

  public static final int SEARCH_PRODUCT_TRIAL_COUNT = 10;

  // TODO: delete in the future
  public static final Map<String, String> CHECK_TARGET_KEYS_PASSWORDS = new HashMap<>();
  static {
    CHECK_TARGET_KEYS_PASSWORDS.put(EC_SITE_AMAZON, "AMAZON_CHECK_TARGET_KEYS_PASSWORDS");
    CHECK_TARGET_KEYS_PASSWORDS.put(EC_SITE_KOJIMA, "KOJIMA_CHECK_TARGET_KEYS_PASSWORDS");
    CHECK_TARGET_KEYS_PASSWORDS.put(EC_SITE_YAHOO,  "YAHOO_CHECK_TARGET_KEYS_PASSWORDS");
  }

  // TODO: delete in the future
  public static final String AMAZON_CHECK_TARGET_KEYS_PASSWORDS = "AMAZON_CHECK_TARGET_KEYS_PASSWORDS";
  public static final String KOJIMA_CHECK_TARGET_KEYS_PASSWORDS = "KOJIMA_CHECK_TARGET_KEYS_PASSWORDS";
  public static final String YAHOO_CHECK_TARGET_KEYS_PASSWORDS  = "YAHOO_CHECK_TARGET_KEYS_PASSWORDS";

  public static final String CHECK_EQUIVALENCE = "Equivalence check";
  public static final String CHECK_FORMAT      = "Format check";

  public static final String CHECK_RESULT_OK = "OK";
  public static final String CHECK_RESULT_NG = "NG";
  public static final String CHECK_RESULT_NOT_MATCH = "NOT-MATCH";
  public static final String CHECK_RESULT_NOT_EXIST = "NOT-EXIST";
  public static final String CHECK_RESULT_NOT_EQUAL = "NOT-EQUAL";
  public static final String CHECK_RESULT_BLANK = "<BLANK>";
}
