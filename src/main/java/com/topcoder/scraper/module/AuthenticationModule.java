package com.topcoder.scraper.module;

import java.io.IOException;

/**
 * abstract authentication module
 */
public abstract class AuthenticationModule implements IBasicModule {

  /**
   * authenticate in a site
   */
  public abstract void authenticate() throws IOException;
}

