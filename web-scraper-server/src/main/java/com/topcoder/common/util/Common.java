package com.topcoder.common.util;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.AuthStatusType;
import com.topcoder.common.model.ECCookie;
import com.topcoder.common.model.ECCookies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * common util class
 */
public class Common {

  /**
   * logger instance
   */
  private static Logger logger = LoggerFactory.getLogger(Common.class.getName());

  /**
   * restore Cookies from ec site to web client
   *
   * @param webClient        the web client instance
   * @param ecSiteAccountDAO the ec site account
   * @return the result
   */
  public static boolean restoreCookies(WebClient webClient, ECSiteAccountDAO ecSiteAccountDAO) {

    webClient.getCookieManager().clearCookies();
    if (ecSiteAccountDAO.getAuthStatus() == null || ecSiteAccountDAO.getAuthStatus().equals(AuthStatusType.FAILED)) {
      logger.warn("skip ecSite id = " + ecSiteAccountDAO.getId() + ", because of auth status is failed");
      return false;
    }

    // restore cookies
    String stringCookies = ecSiteAccountDAO.getEcCookies();
    ECCookies ecCookies = ECCookies.fromJSON(stringCookies);
    if (ecCookies == null || ecCookies.getCookies() == null || ecCookies.getCookies().size() <= 0) {
      logger.warn("skip ecSite id = " + ecSiteAccountDAO.getId() + ", because of parse cook failed");
      return false;
    }

    logger.info("Cookie Information: ");
    for (ECCookie ecCookie : ecCookies.getCookies()) {
      logger.info("* Cookie: " + ecCookie.getName() + "," + ecCookie.getValue()+ "," + ecCookie.getExpires() +": Restore");

      webClient.getCookieManager().addCookie(new Cookie(
              ecCookie.getDomain(), ecCookie.getName(), ecCookie.getValue(), ecCookie.getPath(), ecCookie.getExpires(),
              ecCookie.isSecure(), ecCookie.isHttpOnly()
      ));
    }
    return true;
  }

  /**
   * put cookies into request
   *
   * @param webClient the web client
   * @param url       the url
   */
  public static WebRequest wrapURL(WebClient webClient, String url) throws MalformedURLException {
    StringBuilder cookies = new StringBuilder();
    for (Cookie cookie : webClient.getCookieManager().getCookies()) {
      cookies.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
    }
    WebRequest page = new WebRequest(new URL(url));
    page.setAdditionalHeader("cookie", cookies.substring(0, cookies.length() - 2));
    return page;
  }

  /**
   * get default value
   *
   * @param value        the value
   * @param defaultValue the default value
   * @param <T>          the type
   * @return the value
   */
  public static <T> T getValueOrDefault(T value, T defaultValue) {
    return value == null ? defaultValue : value;
  }
}
