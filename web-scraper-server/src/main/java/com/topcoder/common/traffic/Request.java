package com.topcoder.common.traffic;

import com.gargoylesoftware.htmlunit.Page;

import java.io.IOException;

/**
 * User action request class
 * for example:
 * a. open page
 * b. click some html element
 */
public interface Request {

  /**
   * invoke action
   *
   * @param <P> the page
   * @return the new page
   * @throws IOException invoke failed, like network error
   */
  <P extends Page> P invoke() throws IOException;
}
