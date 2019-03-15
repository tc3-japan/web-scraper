package com.topcoder.scraper.module;

import java.io.IOException;

/**
 * abstract change detection init module
 */
public abstract class ChangeDetectionInitModule implements IBasicModule {

  /**
   * init change detection
   */
  public abstract void init() throws IOException;
}

