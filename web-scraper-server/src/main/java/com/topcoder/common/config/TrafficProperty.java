package com.topcoder.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Traffic definition property
 */
@Component
@PropertySources({
        @PropertySource(factory = YamlPropertySourceFactory.class, value = {"classpath:tactic.yaml"}),
        @PropertySource(factory = YamlPropertySourceFactory.class, value = {"file:${tacticFile}"}, ignoreResourceNotFound = true)
})
@ConfigurationProperties
@Data
public class TrafficProperty {


  /**
   * Represents `request_headers` in tactic.yaml
   */
  @Data
  public static class RequestHeader {
    private List<String> userAgent;
  }

  /**
   * Represents `tactics` in tactic.yaml
   */
  @Data
  public static class Tactic {
    private String usersRange;
    private RequestHeader requestHeaders;
    private Integer requestInterval;
    private Boolean requestIntervalRandom;
    private String proxyServer;
    private Integer retryInterval;
    private Integer retryTrailCount;

    /**
     * check the user id is in range
     *
     * @param id the user id
     * @return the result
     */
    public int getIndexInRange(int id) {
      String[] range = usersRange.split("-");
      if (range.length < 2) {
        return -1;
      }
      int start = Integer.parseInt(range[0]);
      int end = Integer.parseInt(range[1]);
      if (start <= id && end >= id) {
        return id - start;
      }
      return -1;
    }

    /**
     * get the user agent by user id
     *
     * @param id the user id
     * @return the agent
     */
    public String getUserAgent(int id) {
      if (getUsersRange() == null) {
        return requestHeaders.getUserAgent().size() > 0 ? requestHeaders.getUserAgent().get(0) : null;
      }
      int index = getIndexInRange(id);
      if (requestHeaders.getUserAgent().size() <= index) {
        return null;
      }
      return requestHeaders.getUserAgent().get(index);
    }
  }

  /**
   * tactics for users
   */
  private List<Tactic> tactics;

  /**
   * default tactic for default
   */
  private Tactic defaultTactic;
}
