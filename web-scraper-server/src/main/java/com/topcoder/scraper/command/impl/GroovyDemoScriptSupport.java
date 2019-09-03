package com.topcoder.scraper.command.impl;

import com.topcoder.common.util.HtmlUtils;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GroovyDemoScriptSupport extends Script {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserEncoderCommand.class);

  // Wrapper for export methods
  String foo(String[] args) {
    LOGGER.info("[GroovyDemoScriptSupport # foo()]");
    return HtmlUtils.foo(args);
  }
}
