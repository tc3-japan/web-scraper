package com.topcoder.scraper.module;

import com.topcoder.scraper.module.IBasicModule;

import java.io.IOException;
import java.util.List;

/**
 * abstract change detection init module
 */
public abstract class IChangeDetectionInitModule implements IBasicModule {

  /**
   * init change detection
   */
  public abstract void init(List<String> sites) throws IOException;
}

