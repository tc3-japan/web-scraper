package com.topcoder.scraper.module;

import com.topcoder.scraper.module.IBasicModule;

import java.io.IOException;
import java.util.List;

/**
 * abstract change detection init module
 */
public interface IChangeDetectionInitModule extends IBasicModule {

  /**
   * init change detection
   */
  void init(List<String> sites) throws IOException;
}

