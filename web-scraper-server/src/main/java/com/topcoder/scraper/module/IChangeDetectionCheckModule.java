package com.topcoder.scraper.module;

import com.topcoder.scraper.module.IBasicModule;

import java.io.IOException;
import java.util.List;

/**
 * abstract change detection check module
 */
public abstract class IChangeDetectionCheckModule implements IBasicModule {

  /**
   * check change detection
   */
  public abstract void check(List<String> sites) throws IOException;
}

