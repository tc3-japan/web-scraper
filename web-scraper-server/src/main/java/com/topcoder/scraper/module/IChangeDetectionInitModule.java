package com.topcoder.scraper.module;

import com.topcoder.scraper.module.IBasicModule;

import java.io.IOException;

/**
 * abstract change detection init module
 */
public abstract class IChangeDetectionInitModule implements IBasicModule {

  /**
   * init change detection
   */
  public abstract void init() throws IOException;
}

