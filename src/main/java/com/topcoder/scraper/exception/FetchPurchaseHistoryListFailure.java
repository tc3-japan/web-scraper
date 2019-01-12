package com.topcoder.scraper.exception;

public class FetchPurchaseHistoryListFailure extends RuntimeException {
  public FetchPurchaseHistoryListFailure() {
  }

  public FetchPurchaseHistoryListFailure(String message) {
    super(message);
  }
}
