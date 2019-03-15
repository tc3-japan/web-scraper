package com.topcoder.api.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Logging aspect configuration.
 */
@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

  /**
   * Creates logging aspect bean.
   *
   * @return the logging aspect bean
   */
  @Bean
  public LoggingAspect loggingAspect() {
    return new LoggingAspect();
  }
}
