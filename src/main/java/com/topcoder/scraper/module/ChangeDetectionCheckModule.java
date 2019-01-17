package com.topcoder.scraper.module;

import java.io.IOException;

/**
 * abstract change detection check module
 */
public abstract class ChangeDetectionCheckModule implements IBasicModule {

  /**
   * check change detection
   */
  public abstract void check() throws IOException;
}

